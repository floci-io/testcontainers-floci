package io.floci.testcontainers.core;

import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.ResourceReaper;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Base class of the Testcontainers modules for the Floci cloud emulators (e.g. Floci Azure).
 *
 * <p>Holds everything that does not depend on the emulated cloud provider:
 * <ul>
 *     <li>the registry of service configurations (see {@link #registerServiceConfig(AbstractServiceConfig)})
 *     and the wiring of their environment variables, exposed ports and file mounts into the container,</li>
 *     <li>{@link #disableAllServices()},</li>
 *     <li>mounting the host Docker socket only while an enabled service needs it, with an explicit
 *     {@link #withDockerSocket(boolean)} override,</li>
 *     <li>the log level, a dedicated Docker network and a graceful shutdown,</li>
 *     <li>a resource namespace per start, which the emulator puts on every container and volume it creates
 *     (label {@code floci_namespace}), so they are removed on {@link #stop()} and by Ryuk when the JVM dies,</li>
 *     <li>waiting for the emulator's health endpoint and resetting its state via {@link #reset()}.</li>
 * </ul>
 *
 * <p>Subclasses pass the {@link CloudDescriptor} of their emulator to the constructor, register one
 * {@link ServiceConfigRef} per service via {@link #registerServiceConfig(AbstractServiceConfig)} (typically in field
 * initializers), optionally replace the default wait strategy and finally call {@link #applyAllConfigs()} at the end
 * of their constructor.
 *
 * @param <SELF> the concrete container type, used to support a fluent, self-typed API
 */
public abstract class AbstractFlociContainer<SELF extends AbstractFlociContainer<SELF>> extends GenericContainer<SELF> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFlociContainer.class);

    private static final String DOCKER_SOCKET_PATH = "/var/run/docker.sock";
    private static final int STOP_TIMEOUT_SECONDS = 30;
    private static final Level DEFAULT_LOG_LEVEL = Level.WARN;
    private static final Duration DEFAULT_STARTUP_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration RESET_TIMEOUT = Duration.ofSeconds(60);
    private static final String NAMESPACE_LABEL = "floci_namespace";
    private static final String NAMESPACE_PREFIX = "tc-";

    private final CloudDescriptor descriptor;
    private final List<ServiceConfigRef<?>> serviceConfigs = new ArrayList<>();

    // explicit override from withDockerSocket(), which takes full precedence over auto-detection
    private Boolean dockerSocketOverride;

    // the namespace generated for the current start; a caller-set namespace is never replaced
    private String generatedResourceNamespace;

    /**
     * Creates a new Floci container. Applies the descriptor's {@linkplain CloudDescriptor#getDefaultEnv() default
     * environment} first, sets the log level to {@link Level#WARN} and waits for HTTP {@code 200} on the
     * descriptor's {@linkplain CloudDescriptor#getHealthPath() health path}.
     *
     * @param dockerImageName the Docker image to start, which must be compatible with
     *                        {@link CloudDescriptor#getImage()}
     * @param descriptor      the description of the emulator
     */
    protected AbstractFlociContainer(DockerImageName dockerImageName, CloudDescriptor descriptor) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DockerImageName.parse(descriptor.getImage()));

        this.descriptor = descriptor;

        descriptor.getDefaultEnv().forEach(this::withEnv);
        withLogLevel(DEFAULT_LOG_LEVEL);
        waitingFor(Wait.forHttp(descriptor.getHealthPath())
                .forPort(descriptor.getPort())
                .forStatusCode(200)
                .withStartupTimeout(DEFAULT_STARTUP_TIMEOUT));
    }

    /**
     * Registers a service configuration, so that it takes part in {@link #applyAllConfigs()},
     * {@link #disableAllServices()} and the Docker socket auto-detection.
     *
     * @param initialConfig the initial (usually default) configuration of the service
     * @param <C>           the service configuration type
     * @return a reference holding the current configuration of the service
     */
    protected final <C extends AbstractServiceConfig<?>> ServiceConfigRef<C> registerServiceConfig(C initialConfig) {
        ServiceConfigRef<C> ref = new ServiceConfigRef<>(initialConfig);
        serviceConfigs.add(ref);
        return ref;
    }

    /**
     * Replaces the configuration held by {@code ref} with a copy modified by {@code configurer}, and applies the
     * new configuration to the container (exposed ports, environment variables and file mounts).
     *
     * @param ref        the reference of the service configuration to update
     * @param configurer a consumer that receives a builder pre-populated with the current configuration
     * @param <C>        the service configuration type
     * @param <B>        the builder type of the service configuration
     * @return this container instance
     */
    protected final <C extends AbstractServiceConfig<B>, B extends AbstractServiceConfigBuilder<B, C>> SELF updateServiceConfig(
            ServiceConfigRef<C> ref, Consumer<B> configurer) {
        B builder = ref.get().toBuilder();
        configurer.accept(builder);
        ref.set(builder.build());

        configureExposedPorts();
        ref.get().applyEnvVarsToContainer(this);
        ref.get().applyFileMountsToContainer(this);
        return self();
    }

    /**
     * Applies all configurations (global ones via {@link #applyGlobalEnvVars()} and all registered service
     * configurations) to the container. Subclasses call this at the end of their constructor; it is also called
     * by {@link #disableAllServices()}.
     */
    protected final void applyAllConfigs() {
        configureExposedPorts();
        configureEnvVars();
        configureFileMounts();
    }

    /**
     * Hook for subclasses to apply the environment variables of their cross-cutting (non-service)
     * configurations, e.g. TLS. Called by {@link #applyAllConfigs()} before the service configurations are
     * applied. Does nothing by default.
     */
    protected void applyGlobalEnvVars() {
    }

    /**
     * Finalizes container configuration right before creation, once every {@code with*Config(...)} call
     * the caller is going to make has already happened. Used to conditionally mount the Docker socket,
     * since that decision depends on the final state of all service configurations rather than any single
     * one of them.
     */
    @Override
    protected void configure() {
        super.configure();

        Optional<Bind> dockerSocketBinding = findDockerSocketBinding();
        if (shouldBindDockerSocket()) {
            if (dockerSocketBinding.isEmpty()) {
                // Allow creation of child container instances (e.g. for database or Kubernetes services)
                withFileSystemBind(DockerClientFactory.instance().getRemoteDockerUnixSocketPath(), DOCKER_SOCKET_PATH);
            }
        } else {
            dockerSocketBinding.ifPresent(bind -> getBinds().remove(bind));

            if (isDockerSocketRequiredByServices()) {
                logger.warn("There are services requiring a Docker socket, but the Floci container is configured to not mount it. This may cause failures in those services.");
            }
        }
    }

    /**
     * Prepares the resource namespace before the container is created: unless the caller set one or the container
     * is reused, a new namespace is generated for every start and registered with Ryuk, so that the containers and
     * volumes the emulator creates are removed even if the JVM dies.
     */
    @Override
    protected void doStart() {
        prepareResourceNamespace();
        super.doStart();
    }

    private void prepareResourceNamespace() {
        String envVar = descriptor.getResourceNamespaceEnvVar();
        String current = getEnvMap().get(envVar);
        boolean callerSet = current != null && !current.equals(generatedResourceNamespace);

        if (isReusable()) {
            // A generated namespace would change the container hash and prevent reuse; reused siblings must also
            // survive the JVM, so nothing is registered with Ryuk.
            if (!callerSet && current != null) {
                getEnvMap().remove(envVar);
            }
            generatedResourceNamespace = null;
            return;
        }

        if (!callerSet) {
            generatedResourceNamespace = NAMESPACE_PREFIX + uniqueShortId();
            withEnv(envVar, generatedResourceNamespace);
        }
        // Register before the emulator can create siblings
        ResourceReaper.instance().registerLabelsFilterForCleanup(Map.of(NAMESPACE_LABEL, getResourceNamespace()));
    }

    /**
     * Stops the emulator gracefully (so it can tear down what it created), removes the container and then every
     * container and volume left behind with this container's {@code floci_namespace} label. Failures of the
     * sibling cleanup are logged and never fail the stop. Reused containers keep their siblings.
     */
    @Override
    public void stop() {
        String namespace = getContainerId() != null && !isReusable() ? getResourceNamespace() : null;
        beforeStop();
        stopGracefully();
        super.stop();
        try {
            if (namespace != null) {
                removeSiblingResources(namespace);
            }
        } finally {
            afterStop();
        }
    }

    /**
     * Hook for subclasses, called by {@link #stop()} while the emulator may still be running, before it is stopped.
     * Does nothing by default.
     */
    protected void beforeStop() {
    }

    /**
     * Hook for subclasses, called by {@link #stop()} after the container and its siblings have been removed, even
     * if the removal failed. Does nothing by default.
     */
    protected void afterStop() {
    }

    private void removeSiblingResources(String namespace) {
        try {
            for (Container container : dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withLabelFilter(Map.of(NAMESPACE_LABEL, namespace))
                    .exec()) {
                try {
                    dockerClient.removeContainerCmd(container.getId()).withForce(true).withRemoveVolumes(true).exec();
                } catch (NotFoundException ignored) {
                    // The emulator's shutdown or Ryuk may have already removed this container.
                } catch (RuntimeException e) {
                    logger.warn("Failed to remove Floci sibling container {}", container.getId(), e);
                }
            }
            // Docker's container removal only deletes anonymous volumes, so named volumes need a separate pass.
            for (InspectVolumeResponse volume : dockerClient.listVolumesCmd()
                    .withFilter("label", List.of(NAMESPACE_LABEL + "=" + namespace))
                    .exec().getVolumes()) {
                try {
                    dockerClient.removeVolumeCmd(volume.getName()).exec();
                } catch (NotFoundException ignored) {
                    // Container removal or Ryuk may have already removed this volume.
                } catch (RuntimeException e) {
                    logger.warn("Failed to remove Floci sibling volume {}", volume.getName(), e);
                }
            }
        } catch (RuntimeException e) {
            logger.warn("Failed to remove the containers and volumes of Floci namespace {}", namespace, e);
        }
    }

    /**
     * Returns whether this container is reused across test runs (reuse requested and enabled by the environment).
     *
     * @return {@code true} if the container is reused
     */
    protected boolean isReusable() {
        return isShouldBeReused() && TestcontainersConfiguration.getInstance().environmentSupportsReuse();
    }

    private void stopGracefully() {
        if (!isRunning()) {
            return;
        }

        try {
            dockerClient.stopContainerCmd(getContainerId())
                    .withTimeout(STOP_TIMEOUT_SECONDS)
                    .exec();
        } catch (RuntimeException e) {
            logger.warn("Failed to stop Floci gracefully; forcing container removal", e);
        }
    }

    /**
     * Returns the description of the emulator this container runs.
     *
     * @return the cloud descriptor
     */
    public CloudDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Returns the port the emulator serves its API on inside the container.
     *
     * @return the container port of the emulator
     */
    public int getPort() {
        return descriptor.getPort();
    }

    /**
     * Returns the endpoint URL for connecting to the emulator (e.g. {@code http://localhost:32781}).
     *
     * @return the endpoint URL
     */
    public String getEndpoint() {
        return String.format("http://%s:%d", getHost(), getMappedPort(descriptor.getPort()));
    }

    /**
     * Returns the namespace the emulator puts into the names and the {@code floci_namespace} label of every
     * container and volume it creates.
     *
     * <p>Unless set explicitly via the descriptor's
     * {@linkplain CloudDescriptor#getResourceNamespaceEnvVar() environment variable}, a new namespace
     * ({@code tc-} plus 8 hex characters) is generated on every start, so this returns {@code null} before the first
     * start. Reused containers get no generated namespace.
     *
     * @return the resource namespace, or {@code null} if none is set
     */
    public String getResourceNamespace() {
        return getEnvMap().get(descriptor.getResourceNamespaceEnvVar());
    }

    /**
     * Resets the state of the running emulator by sending {@code POST} to the descriptor's
     * {@linkplain CloudDescriptor#getResetPath() reset path}: every resource created so far is removed.
     *
     * @throws IllegalStateException if the container is not running or the emulator does not answer with a
     *                               {@code 2xx} status
     */
    public void reset() {
        if (!isRunning()) {
            throw new IllegalStateException("Cannot reset Floci: the container is not running");
        }

        // Plain HTTP keeps working on the emulator port while TLS is enabled, and needs no trusted certificate
        URI uri = URI.create(String.format("http://%s:%d%s",
                getHost(), getMappedPort(descriptor.getPort()), descriptor.getResetPath()));
        HttpRequest request = HttpRequest.newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(RESET_TIMEOUT)
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to reset Floci via " + uri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while resetting Floci via " + uri, e);
        }
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Failed to reset Floci via " + uri + " (HTTP " + response.statusCode()
                    + "): " + response.body());
        }
    }

    /**
     * Sets the log level for this Floci instance. Defaults to {@link Level#WARN}.
     *
     * @param logLevel the log level
     * @return this container instance
     */
    public SELF withLogLevel(Level logLevel) {
        return withEnv(descriptor.getLogLevelEnvVar(), logLevel.toString());
    }

    /**
     * Returns the log level configured for this Floci instance. Defaults to {@link Level#WARN}.
     *
     * @return the log level
     */
    public Level getLogLevel() {
        String logLevelStr = getEnvMap().getOrDefault(descriptor.getLogLevelEnvVar(), DEFAULT_LOG_LEVEL.toString());
        try {
            return Level.valueOf(logLevelStr);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid log level '{}' in environment variable, defaulting to {}", logLevelStr, DEFAULT_LOG_LEVEL);
            return DEFAULT_LOG_LEVEL;
        }
    }

    /**
     * Configures a dedicated Docker network for this container that will be used by Floci itself and by all
     * services that spin up additional containers.
     *
     * @return this container instance
     */
    public SELF withDedicatedNetwork() {
        String networkName = "floci-network-" + uniqueShortId();
        Network network = Network.builder()
                .createNetworkCmdModifier(cmd -> cmd.withName(networkName))
                .build();
        withNetwork(network);
        return withEnv(descriptor.getNetworkEnvVar(), networkName);
    }

    /**
     * Returns the name of the dedicated Docker network configured for this container, or {@code null} if no
     * dedicated network is configured.
     *
     * @return the name of the dedicated Docker network, or {@code null} if not configured
     */
    public String getDedicatedNetworkName() {
        return getEnvMap().get(descriptor.getNetworkEnvVar());
    }

    /**
     * Disables all services in this Floci container. This is useful for testing scenarios where you want to start
     * the container without any services running and without any extra ports being exposed (e.g. to improve
     * startup performance), and then enable only the services you need.
     *
     * @return this container instance
     */
    public SELF disableAllServices() {
        serviceConfigs.forEach(ServiceConfigRef::disable);
        applyAllConfigs();
        return self();
    }

    /**
     * Returns the current configurations of all registered services, in registration order.
     *
     * @return an unmodifiable snapshot of the current service configurations
     */
    public List<AbstractServiceConfig<?>> getServiceConfigs() {
        return serviceConfigs.stream()
                .<AbstractServiceConfig<?>>map(ServiceConfigRef::get)
                .toList();
    }

    /**
     * Overrides whether the host Docker socket is mounted into the container, bypassing auto-detection.
     *
     * <p>By default (this method never called), the socket is mounted only if at least one currently
     * enabled service actually needs it (see {@link AbstractServiceConfig#requiresDockerSocket()}).
     * Calling this method overrides that auto-detection entirely, regardless of which services are enabled:
     *
     * <ul>
     *     <li>{@code withDockerSocket(false)} — never mount the socket. Useful on hosts where mounting
     *     it is broken or undesired and no Docker-backed service is actually required.</li>
     *     <li>{@code withDockerSocket(true)} — always mount the socket, even if no currently enabled
     *     service is detected as needing it.</li>
     * </ul>
     *
     * @param enabled {@code true} to always mount the Docker socket, {@code false} to never mount it
     * @return this container instance
     */
    public SELF withDockerSocket(boolean enabled) {
        this.dockerSocketOverride = enabled;
        return self();
    }

    private void configureExposedPorts() {
        withExposedPorts(descriptor.getPort());

        serviceConfigs.forEach(ref -> ref.get().applyExposedPortsToContainer(this));
    }

    private void configureEnvVars() {
        applyGlobalEnvVars();

        serviceConfigs.forEach(ref -> ref.get().applyEnvVarsToContainer(this));
    }

    private void configureFileMounts() {
        serviceConfigs.forEach(ref -> ref.get().applyFileMountsToContainer(this));
    }

    private boolean shouldBindDockerSocket() {
        return dockerSocketOverride != null ? dockerSocketOverride : isDockerSocketRequiredByServices();
    }

    private boolean isDockerSocketRequiredByServices() {
        return serviceConfigs.stream().anyMatch(ref -> ref.get().requiresDockerSocket());
    }

    private Optional<Bind> findDockerSocketBinding() {
        return getBinds().stream().filter(b -> DOCKER_SOCKET_PATH.equals(b.getVolume().getPath())).findFirst();
    }

    private static String uniqueShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    /**
     * A mutable reference to the current, immutable configuration of one service. Created via
     * {@link #registerServiceConfig(AbstractServiceConfig)} and updated via
     * {@link #updateServiceConfig(ServiceConfigRef, Consumer)}.
     *
     * @param <C> the service configuration type
     */
    protected static final class ServiceConfigRef<C extends AbstractServiceConfig<?>> {

        private C config;

        private ServiceConfigRef(C config) {
            this.config = config;
        }

        /**
         * Returns the current configuration.
         *
         * @return the current configuration
         */
        public C get() {
            return config;
        }

        private void set(C config) {
            this.config = config;
        }

        @SuppressWarnings("unchecked")
        private void disable() {
            config = (C) config.toBuilder().enabled(false).build();
        }
    }
}

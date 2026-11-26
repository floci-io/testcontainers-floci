package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for ELBv2 (Elastic Load Balancing v2)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ElbV2Config config = ElbV2Config.builder()
 *     .listenerPort(8080)
 *     .build();
 * }</pre>
 */
public class ElbV2Config extends AbstractServiceConfig<ElbV2Config.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final boolean DEFAULT_BACKEND_KEEP_ALIVE = true;
    private static final int DEFAULT_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS = 60;

    private final boolean mock;
    private final List<Integer> listenerPorts;
    private final boolean backendKeepAlive;
    private final int backendKeepAliveTimeoutSeconds;

    private ElbV2Config(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.listenerPorts = List.copyOf(builder.listenerPorts);
        this.backendKeepAlive = builder.backendKeepAlive;
        this.backendKeepAliveTimeoutSeconds = builder.backendKeepAliveTimeoutSeconds;
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether load balancers are simulated without starting a real load balancer
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the pre-declared listener ports to be exposed on the container.
     *
     * @return immutable list of listener ports
     */
    public List<Integer> getListenerPorts() {
        return listenerPorts;
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            for (int port : listenerPorts) {
                container.addExposedPorts(port);
            }
        }
    }

    /**
     * Returns whether backend HTTP/1.x connections are reused between ordinary forwarded requests.
     *
     * @return whether backend HTTP/1.x connections are reused between ordinary forwarded requests
     */
    public boolean isBackendKeepAlive() {
        return backendKeepAlive;
    }

    /**
     * Returns the default idle timeout, in seconds, of pooled backend connections.
     *
     * <p>{@code 0} disables expiry; a target's {@code Keep-Alive} header may override it.
     *
     * @return the default idle timeout, in seconds, of pooled backend connections
     */
    public int getBackendKeepAliveTimeoutSeconds() {
        return backendKeepAliveTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ELBV2_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ELBV2_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE", String.valueOf(backendKeepAlive));
            container.withEnv("FLOCI_SERVICES_ELBV2_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS", String.valueOf(backendKeepAliveTimeoutSeconds));
        }
    }

    /**
     * Builder for {@link ElbV2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ElbV2Config> {

        private boolean mock = DEFAULT_MOCK;
        private final List<Integer> listenerPorts = new ArrayList<>();
        private boolean backendKeepAlive = DEFAULT_BACKEND_KEEP_ALIVE;
        private int backendKeepAliveTimeoutSeconds = DEFAULT_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS;

        private Builder() {
            // Allow instantiation only via ElbV2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ElbV2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ElbV2Config instance) {
            super(instance);
            this.mock = instance.isMock();
            this.listenerPorts.addAll(instance.getListenerPorts());
            this.backendKeepAlive = instance.isBackendKeepAlive();
            this.backendKeepAliveTimeoutSeconds = instance.getBackendKeepAliveTimeoutSeconds();
        }

        /**
         * Sets whether load balancers are simulated without starting a real load balancer
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Adds a listener port to be pre-exposed on the container. Call once per port you intend
         * to create a listener on, so the port is accessible from the host.
         *
         * @param port the listener port to expose
         * @return this builder
         */
        public Builder listenerPort(int port) {
            this.listenerPorts.add(port);
            return this;
        }

        /**
         * Sets whether backend HTTP/1.x connections are reused between ordinary forwarded requests.
         *
         * @param backendKeepAlive whether backend HTTP/1.x connections are reused between ordinary forwarded requests (default {@value DEFAULT_BACKEND_KEEP_ALIVE})
         * @return this builder
         */
        public Builder backendKeepAlive(boolean backendKeepAlive) {
            this.backendKeepAlive = backendKeepAlive;
            return this;
        }

        /**
         * Sets the default idle timeout, in seconds, of pooled backend connections.
         *
         * <p>{@code 0} disables expiry; a target's {@code Keep-Alive} header may override it.
         *
         * @param backendKeepAliveTimeoutSeconds the default idle timeout, in seconds, of pooled backend connections (default {@value DEFAULT_BACKEND_KEEP_ALIVE_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder backendKeepAliveTimeoutSeconds(int backendKeepAliveTimeoutSeconds) {
            this.backendKeepAliveTimeoutSeconds = backendKeepAliveTimeoutSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link ElbV2Config} from this builder.
         *
         * @return the ELBv2 configuration
         */
        @Override
        public ElbV2Config build() {
            return new ElbV2Config(this);
        }
    }
}

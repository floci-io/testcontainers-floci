package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for AppSync-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AppSyncConfig config = AppSyncConfig.builder()
 *     .build();
 * }</pre>
 */
public class AppSyncConfig extends AbstractServiceConfig<AppSyncConfig.Builder> {

    private static final int DEFAULT_SCHEMA_WORKER_THREADS = 4;
    private static final int DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_VTL_MAX_LOOPS = 10000;
    private static final int DEFAULT_VTL_MAX_OUTPUT_CHARS = 1048576;
    private static final long DEFAULT_VTL_TIMEOUT_MILLIS = 5000L;
    private static final String DEFAULT_GRAPHQL_IMAGE = "floci/floci-sidecar-graphql:0.2.0";
    private static final boolean DEFAULT_JS_RUNTIME_ENABLED = true;
    private static final String DEFAULT_JS_RUNTIME_IMAGE = "node:22-alpine";
    private static final String DEFAULT_JS_RUNTIME_CONTAINER_NAME = "appsync-js-runtime";
    private static final int DEFAULT_JS_RUNTIME_PORT = 0;
    private static final int DEFAULT_JS_RUNTIME_START_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS = 30;
    private static final boolean DEFAULT_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET = true;
    private static final boolean DEFAULT_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN = false;

    private final int schemaWorkerThreads;
    private final int schemaWorkerShutdownTimeoutSeconds;
    private final int vtlMaxLoops;
    private final int vtlMaxOutputChars;
    private final long vtlTimeoutMillis;
    private final String graphqlUrl;
    private final String graphqlImage;
    private final boolean jsRuntimeEnabled;
    private final String jsRuntimeUrl;
    private final String jsRuntimeImage;
    private final String jsRuntimeContainerName;
    private final int jsRuntimePort;
    private final int jsRuntimeStartTimeoutSeconds;
    private final int jsRuntimeEvaluationTimeoutSeconds;
    private final boolean jsRuntimeEnforceAppsyncSubset;
    private final boolean jsRuntimeKeepRunningOnShutdown;
    private final String jsRuntimeDockerNetwork;

    private AppSyncConfig(Builder builder) {
        super(builder.enabled);
        this.schemaWorkerThreads = builder.schemaWorkerThreads;
        this.schemaWorkerShutdownTimeoutSeconds = builder.schemaWorkerShutdownTimeoutSeconds;
        this.vtlMaxLoops = builder.vtlMaxLoops;
        this.vtlMaxOutputChars = builder.vtlMaxOutputChars;
        this.vtlTimeoutMillis = builder.vtlTimeoutMillis;
        this.graphqlUrl = builder.graphqlUrl;
        this.graphqlImage = builder.graphqlImage;
        this.jsRuntimeEnabled = builder.jsRuntimeEnabled;
        this.jsRuntimeUrl = builder.jsRuntimeUrl;
        this.jsRuntimeImage = builder.jsRuntimeImage;
        this.jsRuntimeContainerName = builder.jsRuntimeContainerName;
        this.jsRuntimePort = builder.jsRuntimePort;
        this.jsRuntimeStartTimeoutSeconds = builder.jsRuntimeStartTimeoutSeconds;
        this.jsRuntimeEvaluationTimeoutSeconds = builder.jsRuntimeEvaluationTimeoutSeconds;
        this.jsRuntimeEnforceAppsyncSubset = builder.jsRuntimeEnforceAppsyncSubset;
        this.jsRuntimeKeepRunningOnShutdown = builder.jsRuntimeKeepRunningOnShutdown;
        this.jsRuntimeDockerNetwork = builder.jsRuntimeDockerNetwork;
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
     * Returns the number of worker threads used for asynchronous schema creation.
     *
     * @return the number of worker threads
     */
    public int getSchemaWorkerThreads() {
        return schemaWorkerThreads;
    }

    /**
     * Returns the number of seconds to wait for in-flight schema workers on shutdown.
     *
     * @return timeout in seconds
     */
    public int getSchemaWorkerShutdownTimeoutSeconds() {
        return schemaWorkerShutdownTimeoutSeconds;
    }

    /**
     * Returns the maximum number of {@code #foreach} loop iterations allowed in a single VTL resolver
     * template render.
     *
     * <p>A hard backstop against runaway loops.
     *
     * @return the maximum number of {@code #foreach} loop iterations allowed in a single VTL resolver template render
     */
    public int getVtlMaxLoops() {
        return vtlMaxLoops;
    }

    /**
     * Returns the maximum rendered output size, in characters, of a single VTL resolver template render.
     *
     * @return the maximum rendered output size, in characters, of a single VTL resolver template render
     */
    public int getVtlMaxOutputChars() {
        return vtlMaxOutputChars;
    }

    /**
     * Returns the wall-clock execution budget, in milliseconds, of a single VTL resolver template render.
     *
     * @return the wall-clock execution budget, in milliseconds, of a single VTL resolver template render
     */
    public long getVtlTimeoutMillis() {
        return vtlTimeoutMillis;
    }

    /**
     * Returns the URL of an already running GraphQL engine sidecar.
     *
     * <p>When set, Floci uses this URL and skips GraphQL sidecar container management.
     *
     * @return the URL of an already running GraphQL engine sidecar, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getGraphqlUrl() {
        return Optional.ofNullable(graphqlUrl);
    }

    /**
     * Returns the image of the GraphQL engine sidecar container Floci runs to execute GraphQL requests.
     *
     * @return the image of the GraphQL engine sidecar container Floci runs to execute GraphQL requests
     */
    public String getGraphqlImage() {
        return graphqlImage;
    }

    /**
     * Returns whether the Node sidecar that evaluates {@code APPSYNC_JS} resolver code is enabled.
     *
     * <p>The sidecar is started lazily, on the first resolver that needs it. Turned off, a JS resolver fails
     * with an explanatory error instead of silently resolving to null.
     *
     * @return whether the Node sidecar that evaluates {@code APPSYNC_JS} resolver code is enabled
     */
    public boolean isJsRuntimeEnabled() {
        return jsRuntimeEnabled;
    }

    /**
     * Returns the URL of an already running {@code APPSYNC_JS} runtime server.
     *
     * <p>When set, Floci evaluates resolver code against this server and skips container management entirely.
     *
     * @return the URL of an already running {@code APPSYNC_JS} runtime server, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getJsRuntimeUrl() {
        return Optional.ofNullable(jsRuntimeUrl);
    }

    /**
     * Returns the image of the {@code APPSYNC_JS} runtime sidecar container.
     *
     * @return the image of the {@code APPSYNC_JS} runtime sidecar container
     */
    public String getJsRuntimeImage() {
        return jsRuntimeImage;
    }

    /**
     * Returns the container name of the {@code APPSYNC_JS} runtime sidecar.
     *
     * @return the container name of the {@code APPSYNC_JS} runtime sidecar
     */
    public String getJsRuntimeContainerName() {
        return jsRuntimeContainerName;
    }

    /**
     * Returns the host port the {@code APPSYNC_JS} runtime sidecar is published on.
     *
     * <p>{@code 0} lets Docker choose one.
     *
     * @return the host port the {@code APPSYNC_JS} runtime sidecar is published on
     */
    public int getJsRuntimePort() {
        return jsRuntimePort;
    }

    /**
     * Returns the number of seconds to wait for the {@code APPSYNC_JS} runtime sidecar to answer its health
     * probe.
     *
     * @return the number of seconds to wait for the {@code APPSYNC_JS} runtime sidecar to answer its health probe
     */
    public int getJsRuntimeStartTimeoutSeconds() {
        return jsRuntimeStartTimeoutSeconds;
    }

    /**
     * Returns the number of seconds a single {@code APPSYNC_JS} resolver evaluation may take.
     *
     * @return the number of seconds a single {@code APPSYNC_JS} resolver evaluation may take
     */
    public int getJsRuntimeEvaluationTimeoutSeconds() {
        return jsRuntimeEvaluationTimeoutSeconds;
    }

    /**
     * Returns whether resolver code that uses JavaScript the {@code APPSYNC_JS} runtime does not have is
     * rejected before evaluating it.
     *
     * <p>On by default, because the sidecar is real Node and would otherwise accept async functions,
     * promises, classes, try/catch, while loops and Node builtin imports, none of which AWS accepts.
     *
     * @return whether resolver code that uses JavaScript the {@code APPSYNC_JS} runtime does not have is rejected before evaluating it
     */
    public boolean isJsRuntimeEnforceAppsyncSubset() {
        return jsRuntimeEnforceAppsyncSubset;
    }

    /**
     * Returns whether the {@code APPSYNC_JS} runtime sidecar keeps running when Floci stops, so the next
     * start reuses it.
     *
     * @return whether the {@code APPSYNC_JS} runtime sidecar keeps running when Floci stops, so the next start reuses it
     */
    public boolean isJsRuntimeKeepRunningOnShutdown() {
        return jsRuntimeKeepRunningOnShutdown;
    }

    /**
     * Returns the Docker network the {@code APPSYNC_JS} runtime sidecar is attached to.
     *
     * @return the Docker network the {@code APPSYNC_JS} runtime sidecar is attached to, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getJsRuntimeDockerNetwork() {
        return Optional.ofNullable(jsRuntimeDockerNetwork);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APPSYNC_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_THREADS", String.valueOf(schemaWorkerThreads));
            container.withEnv("FLOCI_SERVICES_APPSYNC_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS",
                    String.valueOf(schemaWorkerShutdownTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_APPSYNC_VTL_MAX_LOOPS", String.valueOf(vtlMaxLoops));
            container.withEnv("FLOCI_SERVICES_APPSYNC_VTL_MAX_OUTPUT_CHARS", String.valueOf(vtlMaxOutputChars));
            container.withEnv("FLOCI_SERVICES_APPSYNC_VTL_TIMEOUT_MILLIS", String.valueOf(vtlTimeoutMillis));

            if (graphqlUrl != null) {
                container.withEnv("FLOCI_SERVICES_APPSYNC_GRAPHQL_URL", graphqlUrl);
            }

            container.withEnv("FLOCI_SERVICES_APPSYNC_GRAPHQL_IMAGE", graphqlImage);
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENABLED", String.valueOf(jsRuntimeEnabled));

            if (jsRuntimeUrl != null) {
                container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_URL", jsRuntimeUrl);
            }

            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_IMAGE", jsRuntimeImage);
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_CONTAINER_NAME", jsRuntimeContainerName);
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_PORT", String.valueOf(jsRuntimePort));
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_START_TIMEOUT_SECONDS", String.valueOf(jsRuntimeStartTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS", String.valueOf(jsRuntimeEvaluationTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET", String.valueOf(jsRuntimeEnforceAppsyncSubset));
            container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN", String.valueOf(jsRuntimeKeepRunningOnShutdown));

            if (jsRuntimeDockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_APPSYNC_JS_RUNTIME_DOCKER_NETWORK", jsRuntimeDockerNetwork);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        // GraphQL requests and APPSYNC_JS resolvers are executed by sidecar containers, unless
        // external instances are configured
        boolean graphqlSidecar = graphqlUrl == null;
        boolean jsRuntimeSidecar = jsRuntimeEnabled && jsRuntimeUrl == null;
        return isEnabled() && (graphqlSidecar || jsRuntimeSidecar);
    }

    /**
     * Builder for {@link AppSyncConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AppSyncConfig> {

        private int schemaWorkerThreads = DEFAULT_SCHEMA_WORKER_THREADS;
        private int schemaWorkerShutdownTimeoutSeconds = DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS;
        private int vtlMaxLoops = DEFAULT_VTL_MAX_LOOPS;
        private int vtlMaxOutputChars = DEFAULT_VTL_MAX_OUTPUT_CHARS;
        private long vtlTimeoutMillis = DEFAULT_VTL_TIMEOUT_MILLIS;
        private String graphqlUrl;
        private String graphqlImage = DEFAULT_GRAPHQL_IMAGE;
        private boolean jsRuntimeEnabled = DEFAULT_JS_RUNTIME_ENABLED;
        private String jsRuntimeUrl;
        private String jsRuntimeImage = DEFAULT_JS_RUNTIME_IMAGE;
        private String jsRuntimeContainerName = DEFAULT_JS_RUNTIME_CONTAINER_NAME;
        private int jsRuntimePort = DEFAULT_JS_RUNTIME_PORT;
        private int jsRuntimeStartTimeoutSeconds = DEFAULT_JS_RUNTIME_START_TIMEOUT_SECONDS;
        private int jsRuntimeEvaluationTimeoutSeconds = DEFAULT_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS;
        private boolean jsRuntimeEnforceAppsyncSubset = DEFAULT_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET;
        private boolean jsRuntimeKeepRunningOnShutdown = DEFAULT_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN;
        private String jsRuntimeDockerNetwork;

        private Builder() {
            // Allow instantiation only via AppSyncConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AppSyncConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AppSyncConfig instance) {
            super(instance);
            this.schemaWorkerThreads = instance.getSchemaWorkerThreads();
            this.schemaWorkerShutdownTimeoutSeconds = instance.getSchemaWorkerShutdownTimeoutSeconds();
            this.vtlMaxLoops = instance.getVtlMaxLoops();
            this.vtlMaxOutputChars = instance.getVtlMaxOutputChars();
            this.vtlTimeoutMillis = instance.getVtlTimeoutMillis();
            this.graphqlUrl = instance.getGraphqlUrl().orElse(null);
            this.graphqlImage = instance.getGraphqlImage();
            this.jsRuntimeEnabled = instance.isJsRuntimeEnabled();
            this.jsRuntimeUrl = instance.getJsRuntimeUrl().orElse(null);
            this.jsRuntimeImage = instance.getJsRuntimeImage();
            this.jsRuntimeContainerName = instance.getJsRuntimeContainerName();
            this.jsRuntimePort = instance.getJsRuntimePort();
            this.jsRuntimeStartTimeoutSeconds = instance.getJsRuntimeStartTimeoutSeconds();
            this.jsRuntimeEvaluationTimeoutSeconds = instance.getJsRuntimeEvaluationTimeoutSeconds();
            this.jsRuntimeEnforceAppsyncSubset = instance.isJsRuntimeEnforceAppsyncSubset();
            this.jsRuntimeKeepRunningOnShutdown = instance.isJsRuntimeKeepRunningOnShutdown();
            this.jsRuntimeDockerNetwork = instance.getJsRuntimeDockerNetwork().orElse(null);
        }

        /**
         * Sets the number of worker threads used for asynchronous schema creation.
         *
         * @param schemaWorkerThreads the number of worker threads (default {@value DEFAULT_SCHEMA_WORKER_THREADS})
         * @return this builder
         */
        public Builder schemaWorkerThreads(int schemaWorkerThreads) {
            this.schemaWorkerThreads = schemaWorkerThreads;
            return this;
        }

        /**
         * Sets the number of seconds to wait for in-flight schema workers on shutdown.
         *
         * @param schemaWorkerShutdownTimeoutSeconds timeout in seconds
         *         (default {@value DEFAULT_SCHEMA_WORKER_SHUTDOWN_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder schemaWorkerShutdownTimeoutSeconds(int schemaWorkerShutdownTimeoutSeconds) {
            this.schemaWorkerShutdownTimeoutSeconds = schemaWorkerShutdownTimeoutSeconds;
            return this;
        }

        /**
         * Sets the maximum number of {@code #foreach} loop iterations allowed in a single VTL resolver
         * template render.
         *
         * <p>A hard backstop against runaway loops.
         *
         * @param vtlMaxLoops the maximum number of {@code #foreach} loop iterations allowed in a single VTL resolver template render (default {@value DEFAULT_VTL_MAX_LOOPS})
         * @return this builder
         */
        public Builder vtlMaxLoops(int vtlMaxLoops) {
            this.vtlMaxLoops = vtlMaxLoops;
            return this;
        }

        /**
         * Sets the maximum rendered output size, in characters, of a single VTL resolver template render.
         *
         * @param vtlMaxOutputChars the maximum rendered output size, in characters, of a single VTL resolver template render (default {@value DEFAULT_VTL_MAX_OUTPUT_CHARS})
         * @return this builder
         */
        public Builder vtlMaxOutputChars(int vtlMaxOutputChars) {
            this.vtlMaxOutputChars = vtlMaxOutputChars;
            return this;
        }

        /**
         * Sets the wall-clock execution budget, in milliseconds, of a single VTL resolver template render.
         *
         * @param vtlTimeoutMillis the wall-clock execution budget, in milliseconds, of a single VTL resolver template render (default {@value DEFAULT_VTL_TIMEOUT_MILLIS})
         * @return this builder
         */
        public Builder vtlTimeoutMillis(long vtlTimeoutMillis) {
            this.vtlTimeoutMillis = vtlTimeoutMillis;
            return this;
        }

        /**
         * Sets the URL of an already running GraphQL engine sidecar.
         *
         * <p>When set, Floci uses this URL and skips GraphQL sidecar container management.
         *
         * @param graphqlUrl the URL of an already running GraphQL engine sidecar, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder graphqlUrl(String graphqlUrl) {
            this.graphqlUrl = graphqlUrl;
            return this;
        }

        /**
         * Sets the image of the GraphQL engine sidecar container Floci runs to execute GraphQL requests.
         *
         * @param graphqlImage the image of the GraphQL engine sidecar container Floci runs to execute GraphQL requests (default {@value DEFAULT_GRAPHQL_IMAGE})
         * @return this builder
         */
        public Builder graphqlImage(String graphqlImage) {
            this.graphqlImage = graphqlImage;
            return this;
        }

        /**
         * Sets whether the Node sidecar that evaluates {@code APPSYNC_JS} resolver code is enabled.
         *
         * <p>The sidecar is started lazily, on the first resolver that needs it. Turned off, a JS resolver
         * fails with an explanatory error instead of silently resolving to null.
         *
         * @param jsRuntimeEnabled whether the Node sidecar that evaluates {@code APPSYNC_JS} resolver code is enabled (default {@value DEFAULT_JS_RUNTIME_ENABLED})
         * @return this builder
         */
        public Builder jsRuntimeEnabled(boolean jsRuntimeEnabled) {
            this.jsRuntimeEnabled = jsRuntimeEnabled;
            return this;
        }

        /**
         * Sets the URL of an already running {@code APPSYNC_JS} runtime server.
         *
         * <p>When set, Floci evaluates resolver code against this server and skips container management
         * entirely.
         *
         * @param jsRuntimeUrl the URL of an already running {@code APPSYNC_JS} runtime server, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder jsRuntimeUrl(String jsRuntimeUrl) {
            this.jsRuntimeUrl = jsRuntimeUrl;
            return this;
        }

        /**
         * Sets the image of the {@code APPSYNC_JS} runtime sidecar container.
         *
         * @param jsRuntimeImage the image of the {@code APPSYNC_JS} runtime sidecar container (default {@value DEFAULT_JS_RUNTIME_IMAGE})
         * @return this builder
         */
        public Builder jsRuntimeImage(String jsRuntimeImage) {
            this.jsRuntimeImage = jsRuntimeImage;
            return this;
        }

        /**
         * Sets the container name of the {@code APPSYNC_JS} runtime sidecar.
         *
         * @param jsRuntimeContainerName the container name of the {@code APPSYNC_JS} runtime sidecar (default {@value DEFAULT_JS_RUNTIME_CONTAINER_NAME})
         * @return this builder
         */
        public Builder jsRuntimeContainerName(String jsRuntimeContainerName) {
            this.jsRuntimeContainerName = jsRuntimeContainerName;
            return this;
        }

        /**
         * Sets the host port the {@code APPSYNC_JS} runtime sidecar is published on.
         *
         * <p>{@code 0} lets Docker choose one.
         *
         * @param jsRuntimePort the host port the {@code APPSYNC_JS} runtime sidecar is published on (default {@value DEFAULT_JS_RUNTIME_PORT})
         * @return this builder
         */
        public Builder jsRuntimePort(int jsRuntimePort) {
            this.jsRuntimePort = jsRuntimePort;
            return this;
        }

        /**
         * Sets the number of seconds to wait for the {@code APPSYNC_JS} runtime sidecar to answer its health
         * probe.
         *
         * @param jsRuntimeStartTimeoutSeconds the number of seconds to wait for the {@code APPSYNC_JS} runtime sidecar to answer its health probe (default {@value DEFAULT_JS_RUNTIME_START_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder jsRuntimeStartTimeoutSeconds(int jsRuntimeStartTimeoutSeconds) {
            this.jsRuntimeStartTimeoutSeconds = jsRuntimeStartTimeoutSeconds;
            return this;
        }

        /**
         * Sets the number of seconds a single {@code APPSYNC_JS} resolver evaluation may take.
         *
         * @param jsRuntimeEvaluationTimeoutSeconds the number of seconds a single {@code APPSYNC_JS} resolver evaluation may take (default {@value DEFAULT_JS_RUNTIME_EVALUATION_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder jsRuntimeEvaluationTimeoutSeconds(int jsRuntimeEvaluationTimeoutSeconds) {
            this.jsRuntimeEvaluationTimeoutSeconds = jsRuntimeEvaluationTimeoutSeconds;
            return this;
        }

        /**
         * Sets whether resolver code that uses JavaScript the {@code APPSYNC_JS} runtime does not have is
         * rejected before evaluating it.
         *
         * <p>On by default, because the sidecar is real Node and would otherwise accept async functions,
         * promises, classes, try/catch, while loops and Node builtin imports, none of which AWS accepts.
         *
         * @param jsRuntimeEnforceAppsyncSubset whether resolver code that uses JavaScript the {@code APPSYNC_JS} runtime does not have is rejected before evaluating it (default {@value DEFAULT_JS_RUNTIME_ENFORCE_APPSYNC_SUBSET})
         * @return this builder
         */
        public Builder jsRuntimeEnforceAppsyncSubset(boolean jsRuntimeEnforceAppsyncSubset) {
            this.jsRuntimeEnforceAppsyncSubset = jsRuntimeEnforceAppsyncSubset;
            return this;
        }

        /**
         * Sets whether the {@code APPSYNC_JS} runtime sidecar keeps running when Floci stops, so the next
         * start reuses it.
         *
         * @param jsRuntimeKeepRunningOnShutdown whether the {@code APPSYNC_JS} runtime sidecar keeps running when Floci stops, so the next start reuses it (default {@value DEFAULT_JS_RUNTIME_KEEP_RUNNING_ON_SHUTDOWN})
         * @return this builder
         */
        public Builder jsRuntimeKeepRunningOnShutdown(boolean jsRuntimeKeepRunningOnShutdown) {
            this.jsRuntimeKeepRunningOnShutdown = jsRuntimeKeepRunningOnShutdown;
            return this;
        }

        /**
         * Sets the Docker network the {@code APPSYNC_JS} runtime sidecar is attached to.
         *
         * @param jsRuntimeDockerNetwork the Docker network the {@code APPSYNC_JS} runtime sidecar is attached to, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder jsRuntimeDockerNetwork(String jsRuntimeDockerNetwork) {
            this.jsRuntimeDockerNetwork = jsRuntimeDockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link AppSyncConfig} from this builder.
         *
         * @return the AppSync configuration
         */
        @Override
        public AppSyncConfig build() {
            return new AppSyncConfig(this);
        }
    }
}

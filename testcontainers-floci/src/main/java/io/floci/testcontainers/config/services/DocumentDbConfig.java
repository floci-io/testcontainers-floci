package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for DocumentDB-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DocumentDbConfig config = DocumentDbConfig.builder()
 *     .enabled(true)
 *     .mock(true)
 *     .defaultImage("mongo:8.0")
 *     .dockerNetwork("my-docdb-network")
 *     .build();
 * }</pre>
 */
public class DocumentDbConfig extends AbstractServiceConfig<DocumentDbConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final String DEFAULT_IMAGE = "mongo:7.0";

    private final boolean mock;
    private final String defaultImage;
    private final String dockerNetwork;

    private DocumentDbConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.defaultImage = builder.defaultImage;
        this.dockerNetwork = builder.dockerNetwork;
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
     * Returns whether DocumentDB clusters are simulated in-memory without real Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the default Docker image used for DocumentDB instances.
     *
     * @return the image name
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the Docker network used for DocumentDB containers, or {@code null} if not set.
     *
     * @return the Docker network name, or {@code null}
     */
    public String getDockerNetwork() {
        return dockerNetwork;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_DOCDB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_DOCDB_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_DOCDB_DEFAULT_IMAGE", defaultImage);

            if (dockerNetwork != null) {
                container.withEnv("FLOCI_SERVICES_DOCDB_DOCKER_NETWORK", dockerNetwork);
            }
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Builder for {@link DocumentDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DocumentDbConfig> {

        private boolean mock = DEFAULT_MOCK;
        private String defaultImage = DEFAULT_IMAGE;
        private String dockerNetwork;

        private Builder() {
            // Allow instantiation only via DocumentDbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DocumentDbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DocumentDbConfig instance) {
            super(instance);
            this.mock = instance.isMock();
            this.defaultImage = instance.getDefaultImage();
            this.dockerNetwork = instance.getDockerNetwork();
        }

        /**
         * Sets whether DocumentDB clusters are simulated in-memory without real Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the default Docker image for DocumentDB instances.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the Docker network that DocumentDB containers should join.
         *
         * @param dockerNetwork the network name, or {@code null} to use the default bridge
         * @return this builder
         */
        public Builder dockerNetwork(String dockerNetwork) {
            this.dockerNetwork = dockerNetwork;
            return this;
        }

        /**
         * Creates an immutable {@link DocumentDbConfig} from this builder.
         *
         * @return the DocumentDB configuration
         */
        @Override
        public DocumentDbConfig build() {
            return new DocumentDbConfig(this);
        }
    }
}

package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

/**
 * Configuration for DuckDB-specific settings used by Floci services like Athena.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DuckDbConfig config = DuckDbConfig.builder()
 *     .url("http://duckdb:8080")
 *     .defaultImage("floci/floci-duck:1.0")
 *     .build();
 * }</pre>
 */
public class DuckDbConfig {

    private static final String DEFAULT_IMAGE = "floci/floci-duck:latest";

    private final String url;
    private final String defaultImage;

    private DuckDbConfig(Builder builder) {
        this.url = builder.url;
        this.defaultImage = builder.defaultImage;
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the custom DuckDB URL, or {@code null} if Floci manages the container itself.
     *
     * @return the DuckDB URL, or {@code null}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the default Docker image used for the floci-duck container.
     *
     * @return the image name (default {@value DEFAULT_IMAGE})
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Applies DuckDB configuration as environment variables to the given container.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_DUCK_DEFAULT_IMAGE", defaultImage);

        if (url != null) {
            container.withEnv("FLOCI_SERVICES_DUCK_URL", url);
        }
    }

    /**
     * Builder for {@link DuckDbConfig}.
     */
    public static class Builder {

        private String url;
        private String defaultImage = DEFAULT_IMAGE;

        private Builder() {
            // Allow instantiation only via DuckDbConfig.builder()
        }

        private Builder(DuckDbConfig instance) {
            this.url = instance.url;
            this.defaultImage = instance.defaultImage;
        }

        /**
         * Sets a custom DuckDB URL. When set, Floci uses this URL and skips floci-duck container management.
         *
         * @param url the DuckDB URL, or {@code null} to let Floci manage the container
         * @return this builder
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the default Docker image for the floci-duck container.
         *
         * @param defaultImage the image name (default {@value DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Creates an immutable {@link DuckDbConfig} from this builder.
         *
         * @return the DuckDB configuration
         */
        public DuckDbConfig build() {
            return new DuckDbConfig(this);
        }
    }
}

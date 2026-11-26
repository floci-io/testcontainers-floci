package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for QuickSight-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * QuickSightConfig config = QuickSightConfig.builder()
 *     .embedBaseUrl("https://quicksight.example.test")
 *     .build();
 * }</pre>
 */
public class QuickSightConfig extends AbstractServiceConfig<QuickSightConfig.Builder> {

    private final String embedBaseUrl;

    private QuickSightConfig(Builder builder) {
        super(builder);
        this.embedBaseUrl = builder.embedBaseUrl;
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
     * Returns the public origin used by browser embedding clients in the embed URLs Floci generates.
     *
     * @return the public origin used by browser embedding clients in the embed URLs Floci generates, or empty to keep Floci's default
     */
    public Optional<String> getEmbedBaseUrl() {
        return Optional.ofNullable(embedBaseUrl);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_QUICKSIGHT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (embedBaseUrl != null) {
                container.withEnv("FLOCI_SERVICES_QUICKSIGHT_EMBED_BASE_URL", embedBaseUrl);
            }
        }
    }

    /**
     * Builder for {@link QuickSightConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, QuickSightConfig> {

        private String embedBaseUrl;

        private Builder() {
            // Allow instantiation only via QuickSightConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link QuickSightConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(QuickSightConfig instance) {
            super(instance);
            this.embedBaseUrl = instance.getEmbedBaseUrl().orElse(null);
        }

        /**
         * Sets the public origin used by browser embedding clients in the embed URLs Floci generates.
         *
         * @param embedBaseUrl the public origin used by browser embedding clients in the embed URLs Floci generates, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder embedBaseUrl(String embedBaseUrl) {
            this.embedBaseUrl = embedBaseUrl;
            return this;
        }

        /**
         * Creates an immutable {@link QuickSightConfig} from this builder.
         *
         * @return the QuickSight configuration
         */
        @Override
        public QuickSightConfig build() {
            return new QuickSightConfig(this);
        }
    }
}

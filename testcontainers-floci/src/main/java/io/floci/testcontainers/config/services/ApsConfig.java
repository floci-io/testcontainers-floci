package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for APS (Amazon Managed Service for Prometheus)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ApsConfig config = ApsConfig.builder()
 *     .build();
 * }</pre>
 */
public class ApsConfig extends AbstractServiceConfig<ApsConfig.Builder> {

    private ApsConfig(Builder builder) {
        super(builder.enabled);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_APS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ApsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ApsConfig> {

        private Builder() {
            // Allow instantiation only via ApsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ApsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ApsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ApsConfig} from this builder.
         *
         * @return the APS (Amazon Managed Service for Prometheus) configuration
         */
        @Override
        public ApsConfig build() {
            return new ApsConfig(this);
        }
    }
}

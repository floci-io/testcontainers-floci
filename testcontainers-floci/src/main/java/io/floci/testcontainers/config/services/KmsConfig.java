package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for KMS-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KmsConfig config = KmsConfig.builder()
 *     .build();
 * }</pre>
 */
public class KmsConfig extends AbstractServiceConfig<KmsConfig.Builder> {

    private KmsConfig(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_KMS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link KmsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KmsConfig> {


        private Builder() {
            // Allow instantiation only via KmsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KmsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KmsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link KmsConfig} from this builder.
         *
         * @return the KMS configuration
         */
        public KmsConfig build() {
            return new KmsConfig(this);
        }
    }
}

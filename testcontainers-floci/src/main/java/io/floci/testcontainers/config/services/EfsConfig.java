package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EFS (Elastic File System)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EfsConfig config = EfsConfig.builder()
 *     .build();
 * }</pre>
 */
public class EfsConfig extends AbstractServiceConfig<EfsConfig.Builder> {

    private EfsConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_EFS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link EfsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EfsConfig> {

        private Builder() {
            // Allow instantiation only via EfsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EfsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EfsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link EfsConfig} from this builder.
         *
         * @return the EFS (Elastic File System) configuration
         */
        @Override
        public EfsConfig build() {
            return new EfsConfig(this);
        }
    }
}

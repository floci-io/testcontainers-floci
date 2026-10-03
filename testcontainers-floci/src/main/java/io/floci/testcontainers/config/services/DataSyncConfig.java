package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for DataSync-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DataSyncConfig config = DataSyncConfig.builder()
 *     .build();
 * }</pre>
 */
public class DataSyncConfig extends AbstractServiceConfig<DataSyncConfig.Builder> {

    private DataSyncConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_DATASYNC_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DataSyncConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DataSyncConfig> {

        private Builder() {
            // Allow instantiation only via DataSyncConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DataSyncConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DataSyncConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DataSyncConfig} from this builder.
         *
         * @return the DataSync configuration
         */
        @Override
        public DataSyncConfig build() {
            return new DataSyncConfig(this);
        }
    }
}

package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Datastore of Floci GCP.
 *
 * <p>Datastore (Firestore in Datastore mode) is served on the main port. Clients connect via
 * {@code FlociGcpContainer#getEmulatorHost()}, e.g. as {@code DATASTORE_EMULATOR_HOST}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DatastoreConfig config = DatastoreConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class DatastoreConfig extends AbstractServiceConfig<DatastoreConfig.Builder> {

    private DatastoreConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_GCP_SERVICES_DATASTORE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DatastoreConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DatastoreConfig> {

        private Builder() {
            // Allow instantiation only via DatastoreConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DatastoreConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DatastoreConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DatastoreConfig} from this builder.
         *
         * @return the Datastore configuration
         */
        @Override
        public DatastoreConfig build() {
            return new DatastoreConfig(this);
        }
    }
}

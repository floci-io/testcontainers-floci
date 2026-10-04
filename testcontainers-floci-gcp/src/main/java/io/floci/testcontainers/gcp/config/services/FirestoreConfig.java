package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Firestore of Floci GCP.
 *
 * <p>Firestore (native mode) is served via gRPC on the main port. Clients connect via
 * {@code FlociGcpContainer#getEmulatorHost()}, e.g. as {@code FIRESTORE_EMULATOR_HOST}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FirestoreConfig config = FirestoreConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class FirestoreConfig extends AbstractServiceConfig<FirestoreConfig.Builder> {

    private FirestoreConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_FIRESTORE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link FirestoreConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FirestoreConfig> {

        private Builder() {
            // Allow instantiation only via FirestoreConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FirestoreConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FirestoreConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link FirestoreConfig} from this builder.
         *
         * @return the Firestore configuration
         */
        @Override
        public FirestoreConfig build() {
            return new FirestoreConfig(this);
        }
    }
}

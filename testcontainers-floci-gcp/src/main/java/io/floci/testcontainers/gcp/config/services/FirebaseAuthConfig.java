package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Firebase Authentication of Floci GCP.
 *
 * <p>Firebase Authentication (Identity Toolkit) is served via REST on the main port. Clients connect via
 * {@code FlociGcpContainer#getEmulatorHost()}, e.g. as {@code FIREBASE_AUTH_EMULATOR_HOST}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * FirebaseAuthConfig config = FirebaseAuthConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class FirebaseAuthConfig extends AbstractServiceConfig<FirebaseAuthConfig.Builder> {

    private FirebaseAuthConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_FIREBASEAUTH_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link FirebaseAuthConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, FirebaseAuthConfig> {

        private Builder() {
            // Allow instantiation only via FirebaseAuthConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link FirebaseAuthConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(FirebaseAuthConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link FirebaseAuthConfig} from this builder.
         *
         * @return the Firebase Authentication configuration
         */
        @Override
        public FirebaseAuthConfig build() {
            return new FirebaseAuthConfig(this);
        }
    }
}

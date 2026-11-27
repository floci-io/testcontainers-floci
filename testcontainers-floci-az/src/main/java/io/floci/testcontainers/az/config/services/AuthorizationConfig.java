package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure role-based access control of Floci Azure.
 *
 * <p>Azure RBAC under {@code Microsoft.Authorization}: Azure's built-in role definitions and the
 * role assignments clients create. Assignments are stored and answered as Azure does, but nothing
 * enforces them.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AuthorizationConfig config = AuthorizationConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class AuthorizationConfig extends AbstractServiceConfig<AuthorizationConfig.Builder> {

    private AuthorizationConfig(Builder builder) {
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
        container.withEnv("FLOCI_AZ_SERVICES_AUTHORIZATION_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link AuthorizationConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AuthorizationConfig> {

        private Builder() {
            // Allow instantiation only via AuthorizationConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AuthorizationConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AuthorizationConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link AuthorizationConfig} from this builder.
         *
         * @return the Authorization configuration
         */
        @Override
        public AuthorizationConfig build() {
            return new AuthorizationConfig(this);
        }
    }
}

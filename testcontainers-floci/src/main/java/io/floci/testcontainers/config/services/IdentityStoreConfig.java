package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Identity Store-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IdentityStoreConfig config = IdentityStoreConfig.builder()
 *     .build();
 * }</pre>
 */
public class IdentityStoreConfig extends AbstractServiceConfig<IdentityStoreConfig.Builder> {

    private static final String DEFAULT_SCIM_BEARER_TOKEN = "floci-scim-token";

    private final String scimBearerToken;

    private IdentityStoreConfig(Builder builder) {
        super(builder.enabled);
        this.scimBearerToken = builder.scimBearerToken;
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
     * Returns the bearer token IAM Identity Center SCIM v2 requests ({@code /{tenant_id}/scim/v2}) are
     * validated against.
     *
     * <p>Floci does not provision real IAM Identity Center access tokens, so SCIM clients authenticate with
     * this local token.
     *
     * @return the bearer token IAM Identity Center SCIM v2 requests ({@code /{tenant_id}/scim/v2}) are validated against
     */
    public String getScimBearerToken() {
        return scimBearerToken;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_IDENTITYSTORE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_IDENTITYSTORE_SCIM_BEARER_TOKEN", scimBearerToken);
        }
    }

    /**
     * Builder for {@link IdentityStoreConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IdentityStoreConfig> {

        private String scimBearerToken = DEFAULT_SCIM_BEARER_TOKEN;

        private Builder() {
            // Allow instantiation only via IdentityStoreConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IdentityStoreConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IdentityStoreConfig instance) {
            super(instance);
            this.scimBearerToken = instance.getScimBearerToken();
        }

        /**
         * Sets the bearer token IAM Identity Center SCIM v2 requests ({@code /{tenant_id}/scim/v2}) are
         * validated against.
         *
         * <p>Floci does not provision real IAM Identity Center access tokens, so SCIM clients authenticate
         * with this local token.
         *
         * @param scimBearerToken the bearer token IAM Identity Center SCIM v2 requests ({@code /{tenant_id}/scim/v2}) are validated against (default {@value DEFAULT_SCIM_BEARER_TOKEN})
         * @return this builder
         */
        public Builder scimBearerToken(String scimBearerToken) {
            this.scimBearerToken = scimBearerToken;
            return this;
        }

        /**
         * Creates an immutable {@link IdentityStoreConfig} from this builder.
         *
         * @return the Identity Store configuration
         */
        @Override
        public IdentityStoreConfig build() {
            return new IdentityStoreConfig(this);
        }
    }
}

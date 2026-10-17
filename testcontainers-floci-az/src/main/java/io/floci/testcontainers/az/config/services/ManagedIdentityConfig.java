package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Azure Managed Identity of Floci Azure.
 *
 * <p>User-assigned identities, federated identity credentials, the IMDS token endpoint
 * ({@code /metadata/identity/oauth2/token}) and the App Service token endpoint ({@code /msi/token}),
 * issuing tokens signed with the Entra ID key.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ManagedIdentityConfig config = ManagedIdentityConfig.builder()
 *     .systemAssignedScope("subscriptions/00000000-0000-0000-0000-000000000001/resourceGroups/rg/providers/Microsoft.Web/sites/app")
 *     .appServiceHeaderSecret("my-identity-header")
 *     .build();
 * }</pre>
 */
public class ManagedIdentityConfig extends AbstractServiceConfig<ManagedIdentityConfig.Builder> {

    private static final boolean DEFAULT_APP_SERVICE_ENABLED = true;
    private static final String DEFAULT_APP_SERVICE_PATH = "msi/token";
    private static final String DEFAULT_APP_SERVICE_HEADER_SECRET = "floci-az-msi-secret";

    private final String systemAssignedScope;
    private final boolean appServiceEnabled;
    private final String appServicePath;
    private final String appServiceHeaderSecret;

    private ManagedIdentityConfig(Builder builder) {
        super(builder);
        this.systemAssignedScope = builder.systemAssignedScope;
        this.appServiceEnabled = builder.appServiceEnabled;
        this.appServicePath = builder.appServicePath;
        this.appServiceHeaderSecret = builder.appServiceHeaderSecret;
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
     * Returns the ARM scope that seeds the system-assigned IMDS identity. When empty, floci-az
     * uses {@code subscriptions/{default subscription id}}.
     *
     * @return the the system-assigned identity scope, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getSystemAssignedScope() {
        return Optional.ofNullable(systemAssignedScope);
    }

    /**
     * Returns whether the App Service Managed Identity token endpoint is enabled. It is used by
     * credential chains in App Service mode (e.g. Go {@code azidentity}), which read the
     * {@code IDENTITY_ENDPOINT}/{@code IDENTITY_HEADER} environment variables and send the header
     * value as {@code X-IDENTITY-HEADER}.
     *
     * @return {@code true} if the App Service token endpoint is enabled
     */
    public boolean isAppServiceEnabled() {
        return appServiceEnabled;
    }

    /**
     * Returns the path of the App Service Managed Identity token endpoint, without leading slash
     * (e.g. {@code msi/token}, served at {@code http://<host>:<port>/msi/token}).
     *
     * @return the App Service token endpoint path
     */
    public String getAppServicePath() {
        return appServicePath;
    }

    /**
     * Returns the secret the {@code X-IDENTITY-HEADER} header of App Service Managed Identity token
     * requests must carry, i.e. the value for {@code IDENTITY_HEADER}.
     *
     * @return the App Service identity header secret
     */
    public String getAppServiceHeaderSecret() {
        return appServiceHeaderSecret;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_ENABLED", String.valueOf(appServiceEnabled));
            container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_PATH", appServicePath);
            container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_APP_SERVICE_HEADER_SECRET", appServiceHeaderSecret);

            if (systemAssignedScope != null) {
                container.withEnv("FLOCI_AZ_SERVICES_MANAGED_IDENTITY_SYSTEM_ASSIGNED_SCOPE", systemAssignedScope);
            }
        }
    }

    /**
     * Builder for {@link ManagedIdentityConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ManagedIdentityConfig> {

        private String systemAssignedScope;
        private boolean appServiceEnabled = DEFAULT_APP_SERVICE_ENABLED;
        private String appServicePath = DEFAULT_APP_SERVICE_PATH;
        private String appServiceHeaderSecret = DEFAULT_APP_SERVICE_HEADER_SECRET;

        private Builder() {
            // Allow instantiation only via ManagedIdentityConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ManagedIdentityConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ManagedIdentityConfig instance) {
            super(instance);
            this.systemAssignedScope = instance.systemAssignedScope;
            this.appServiceEnabled = instance.appServiceEnabled;
            this.appServicePath = instance.appServicePath;
            this.appServiceHeaderSecret = instance.appServiceHeaderSecret;
        }

        /**
         * Sets the ARM scope that seeds the system-assigned IMDS identity, e.g. the scope of the
         * resource the tested code pretends to run on. When not set, floci-az uses
         * {@code subscriptions/{default subscription id}}.
         *
         * @param systemAssignedScope the system-assigned identity scope ({@code null} to unset)
         * @return this builder
         */
        public Builder systemAssignedScope(String systemAssignedScope) {
            this.systemAssignedScope = systemAssignedScope;
            return this;
        }

        /**
         * Sets whether the App Service Managed Identity token endpoint is enabled. It is used by
         * credential chains in App Service mode (e.g. Go {@code azidentity}), which read the
         * {@code IDENTITY_ENDPOINT}/{@code IDENTITY_HEADER} environment variables and send the header
         * value as {@code X-IDENTITY-HEADER}.
         *
         * @param appServiceEnabled {@code true} to enable the App Service token endpoint
         *                          (default {@value DEFAULT_APP_SERVICE_ENABLED})
         * @return this builder
         */
        public Builder appServiceEnabled(boolean appServiceEnabled) {
            this.appServiceEnabled = appServiceEnabled;
            return this;
        }

        /**
         * Sets the path of the App Service Managed Identity token endpoint, without leading slash.
         *
         * @param appServicePath the App Service token endpoint path (default {@value DEFAULT_APP_SERVICE_PATH})
         * @return this builder
         */
        public Builder appServicePath(String appServicePath) {
            this.appServicePath = appServicePath;
            return this;
        }

        /**
         * Sets the secret the {@code X-IDENTITY-HEADER} header of App Service Managed Identity token
         * requests must carry, i.e. the value for {@code IDENTITY_HEADER}.
         *
         * @param appServiceHeaderSecret the App Service identity header secret
         *                               (default {@value DEFAULT_APP_SERVICE_HEADER_SECRET})
         * @return this builder
         */
        public Builder appServiceHeaderSecret(String appServiceHeaderSecret) {
            this.appServiceHeaderSecret = appServiceHeaderSecret;
            return this;
        }

        /**
         * Creates an immutable {@link ManagedIdentityConfig} from this builder.
         *
         * @return the Managed Identity configuration
         */
        @Override
        public ManagedIdentityConfig build() {
            return new ManagedIdentityConfig(this);
        }
    }
}

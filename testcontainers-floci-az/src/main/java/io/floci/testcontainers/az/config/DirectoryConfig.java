package io.floci.testcontainers.az.config;

import org.testcontainers.containers.Container;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tenant and subscription directory of the Floci Azure server (root properties {@code cloud.default},
 * {@code tenants} and {@code subscriptions}).
 *
 * <p>The directory always holds the default tenant (the Entra ID {@code defaultTenantId}) and the default
 * subscription (the ARM {@code defaultSubscriptionId}). This configuration adds further tenants and
 * subscriptions; an entry with the id of the default tenant or subscription overrides it in place, e.g.
 * to start the default subscription {@code Disabled}. Configured subscriptions are listed by
 * {@code GET /subscriptions} after the default one, in configuration order.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DirectoryConfig config = DirectoryConfig.builder()
 *     .defaultCloud("AzureCloud")
 *     .tenant("11111111-0000-0000-0000-000000000000", t -> t.displayName("Contoso Gov").cloud("AzureUSGovernment"))
 *     .subscription("22222222-0000-0000-0000-000000000000", s -> s.tenantId("11111111-0000-0000-0000-000000000000"))
 *     .build();
 * }</pre>
 */
public class DirectoryConfig {

    private static final String DEFAULT_DEFAULT_CLOUD = "AzureCloud";

    private final String defaultCloud;
    private final List<TenantConfig> tenants;
    private final List<SubscriptionConfig> subscriptions;

    private DirectoryConfig(Builder builder) {
        this.defaultCloud = builder.defaultCloud;
        this.tenants = List.copyOf(builder.tenants.values());
        this.subscriptions = List.copyOf(builder.subscriptions.values());
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

    /**
     * Returns the cloud of the default tenant and of every tenant that names none: {@code AzureCloud},
     * {@code AzureUSGovernment} or {@code AzureChinaCloud}.
     *
     * @return the default cloud name
     */
    public String getDefaultCloud() {
        return defaultCloud;
    }

    /**
     * Returns the tenants the directory holds besides the default one, in configuration order.
     *
     * @return the configured tenants, never {@code null}
     */
    public List<TenantConfig> getTenants() {
        return tenants;
    }

    /**
     * Returns the subscriptions the directory holds besides the default one, in configuration order.
     *
     * @return the configured subscriptions, never {@code null}
     */
    public List<SubscriptionConfig> getSubscriptions() {
        return subscriptions;
    }

    /**
     * Applies the directory environment variables to the given container. Tenants and subscriptions are
     * emitted as indexed properties ({@code FLOCI_AZ_TENANTS_<n>__ID}, {@code FLOCI_AZ_SUBSCRIPTIONS_<n>__ID},
     * …); optional properties only when set.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_CLOUD_DEFAULT", defaultCloud);

        for (int i = 0; i < tenants.size(); i++) {
            TenantConfig tenant = tenants.get(i);
            String prefix = "FLOCI_AZ_TENANTS_" + i + "__";
            container.withEnv(prefix + "ID", tenant.getId());
            tenant.getDisplayName().ifPresent(value -> container.withEnv(prefix + "DISPLAY_NAME", value));
            tenant.getDefaultDomain().ifPresent(value -> container.withEnv(prefix + "DEFAULT_DOMAIN", value));
            tenant.getCloud().ifPresent(value -> container.withEnv(prefix + "CLOUD", value));
        }

        for (int i = 0; i < subscriptions.size(); i++) {
            SubscriptionConfig subscription = subscriptions.get(i);
            String prefix = "FLOCI_AZ_SUBSCRIPTIONS_" + i + "__";
            container.withEnv(prefix + "ID", subscription.getId());
            subscription.getTenantId().ifPresent(value -> container.withEnv(prefix + "TENANT_ID", value));
            subscription.getDisplayName().ifPresent(value -> container.withEnv(prefix + "DISPLAY_NAME", value));
            subscription.getState().ifPresent(value -> container.withEnv(prefix + "STATE", value));
        }
    }

    /**
     * Builder for {@link DirectoryConfig}.
     */
    public static class Builder {

        private String defaultCloud = DEFAULT_DEFAULT_CLOUD;
        private final Map<String, TenantConfig> tenants = new LinkedHashMap<>();
        private final Map<String, SubscriptionConfig> subscriptions = new LinkedHashMap<>();

        private Builder() {
            // Allow instantiation only via DirectoryConfig.builder()
        }

        private Builder(DirectoryConfig instance) {
            this.defaultCloud = instance.defaultCloud;
            instance.tenants.forEach(tenant -> this.tenants.put(tenant.getId(), tenant));
            instance.subscriptions.forEach(subscription -> this.subscriptions.put(subscription.getId(), subscription));
        }

        /**
         * Sets the cloud of the default tenant and of every tenant that names none: {@code AzureCloud},
         * {@code AzureUSGovernment} or {@code AzureChinaCloud}. An unknown name makes Floci Azure fail at
         * startup. Floci Azure currently only resolves tenants, request scopes and the default location of
         * new resource groups to this cloud; endpoints, token issuers and DNS suffixes stay the public
         * cloud's.
         *
         * @param defaultCloud the default cloud name (default {@value DEFAULT_DEFAULT_CLOUD})
         * @return this builder
         */
        public Builder defaultCloud(String defaultCloud) {
            this.defaultCloud = defaultCloud;
            return this;
        }

        /**
         * Adds a tenant with Floci Azure's defaults for all properties but the id.
         *
         * @param id the tenant id
         * @return this builder
         * @see #tenant(String, Consumer)
         */
        public Builder tenant(String id) {
            return tenant(id, tenant -> {
            });
        }

        /**
         * Adds a tenant to the directory. Adding a tenant with an id that was already added replaces the
         * earlier entry, keeping its position. An entry with the id of the default tenant overrides its
         * display name, domain or cloud.
         *
         * @param id         the tenant id
         * @param configurer a consumer that receives a {@link TenantConfig.Builder} to set the optional
         *                   properties
         * @return this builder
         */
        public Builder tenant(String id, Consumer<TenantConfig.Builder> configurer) {
            TenantConfig.Builder builder = TenantConfig.builder(id);
            configurer.accept(builder);
            this.tenants.put(id, builder.build());
            return this;
        }

        /**
         * Adds a subscription with Floci Azure's defaults for all properties but the id.
         *
         * @param id the subscription id
         * @return this builder
         * @see #subscription(String, Consumer)
         */
        public Builder subscription(String id) {
            return subscription(id, subscription -> {
            });
        }

        /**
         * Adds a subscription to the directory. Adding a subscription with an id that was already added
         * replaces the earlier entry, keeping its position. An entry with the id of the default
         * subscription overrides its tenant, name or state.
         *
         * @param id         the subscription id
         * @param configurer a consumer that receives a {@link SubscriptionConfig.Builder} to set the
         *                   optional properties
         * @return this builder
         */
        public Builder subscription(String id, Consumer<SubscriptionConfig.Builder> configurer) {
            SubscriptionConfig.Builder builder = SubscriptionConfig.builder(id);
            configurer.accept(builder);
            this.subscriptions.put(id, builder.build());
            return this;
        }

        /**
         * Creates an immutable {@link DirectoryConfig} from this builder.
         *
         * @return the directory configuration
         */
        public DirectoryConfig build() {
            return new DirectoryConfig(this);
        }
    }
}

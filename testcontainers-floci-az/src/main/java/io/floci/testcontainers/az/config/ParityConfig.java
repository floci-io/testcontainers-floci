package io.floci.testcontainers.az.config;

import org.testcontainers.containers.Container;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Azure parity settings of the Floci Azure server (root properties {@code parity} and
 * {@code parity-overrides}): how closely Floci Azure follows Azure where Azure differs from what Floci
 * Azure has always answered.
 *
 * <p>The {@linkplain Level levels} nest, and every override defaults from the level, so a single
 * override can also be set alone to opt in piecemeal.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ParityConfig config = ParityConfig.builder()
 *     .level(ParityConfig.Level.AZURE)
 *     .strictSubscriptions(true)
 *     .build();
 * }</pre>
 */
public class ParityConfig {

    /**
     * Azure parity level of Floci Azure.
     */
    public enum Level {
        /** Every response a client could rely on stays as Floci Azure has always answered it. */
        COMPAT,
        /** Azure's answers for the requests Azure accepts: every non-strict override on. */
        AZURE,
        /** {@link #AZURE} plus Azure's refusals: every override on, including the {@code strict*} ones. */
        STRICT
    }

    private static final Level DEFAULT_LEVEL = Level.COMPAT;

    private final Level level;
    private final Boolean strictTokens;
    private final Boolean subscriptionListing;
    private final Boolean strictSubscriptions;
    private final Boolean resourceGroupSemantics;
    private final Boolean resourceGroupDelete;
    private final Boolean strictResourceGroups;
    private final Boolean strictProviders;
    private final Boolean strictNames;
    private final Boolean locationNormalize;
    private final Boolean strictLocations;
    private final Boolean tenantScoping;

    private ParityConfig(Builder builder) {
        this.level = builder.level;
        this.strictTokens = builder.strictTokens;
        this.subscriptionListing = builder.subscriptionListing;
        this.strictSubscriptions = builder.strictSubscriptions;
        this.resourceGroupSemantics = builder.resourceGroupSemantics;
        this.resourceGroupDelete = builder.resourceGroupDelete;
        this.strictResourceGroups = builder.strictResourceGroups;
        this.strictProviders = builder.strictProviders;
        this.strictNames = builder.strictNames;
        this.locationNormalize = builder.locationNormalize;
        this.strictLocations = builder.strictLocations;
        this.tenantScoping = builder.tenantScoping;
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
     * Returns the Azure parity level, which every override defaults from.
     *
     * @return the parity level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Returns whether an ARM request whose Bearer token is missing, not signed by the Entra signing key,
     * expired, or issued for another audience is refused with {@code 401}. When empty, it is on from
     * {@link Level#STRICT} or when Entra's {@code validateTokens} is set.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictTokens() {
        return Optional.ofNullable(strictTokens);
    }

    /**
     * Returns whether {@code GET /subscriptions} also lists subscriptions registered on first use, and
     * {@code GET /subscriptions/{sub}} echoes the directory's id rather than the path's spelling. When
     * empty, it is on from {@link Level#AZURE}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getSubscriptionListing() {
        return Optional.ofNullable(subscriptionListing);
    }

    /**
     * Returns whether an ARM request naming a subscription the directory does not hold is refused with
     * {@code 404 SubscriptionNotFound} instead of registering it on first use. When empty, it is on from
     * {@link Level#STRICT}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictSubscriptions() {
        return Optional.ofNullable(strictSubscriptions);
    }

    /**
     * Returns whether resource groups behave as Azure's: a re-PUT answers {@code 200} rather than
     * {@code 201}, a {@code GET} of a missing group is {@code 404 ResourceGroupNotFound}, a child PUT under
     * a missing group creates the group, and groups are kept in the directory store. When empty, it is on
     * from {@link Level#AZURE}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getResourceGroupSemantics() {
        return Optional.ofNullable(resourceGroupSemantics);
    }

    /**
     * Returns whether a resource-group {@code DELETE} is Azure's long-running operation: {@code 202} with a
     * {@code Location} to poll, every resource in the group deleted first, and
     * {@code 404 ResourceGroupNotFound} for a missing group. When empty, it is on from {@link Level#AZURE}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getResourceGroupDelete() {
        return Optional.ofNullable(resourceGroupDelete);
    }

    /**
     * Returns whether a request for a resource under a resource group that does not exist (a PUT, a read,
     * an action or a delete) is refused with {@code 404 ResourceGroupNotFound}. When empty, it is on from
     * {@link Level#STRICT}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictResourceGroups() {
        return Optional.ofNullable(strictResourceGroups);
    }

    /**
     * Returns whether resource providers start {@code NotRegistered} in each subscription, so a request for
     * a resource type of an unregistered namespace is refused with {@code 409 MissingSubscriptionRegistration}
     * until the namespace is registered. When empty, it is on from {@link Level#STRICT}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictProviders() {
        return Optional.ofNullable(strictProviders);
    }

    /**
     * Returns whether a PUT whose resource name breaks its type's naming rule is refused with {@code 400}
     * before its handler runs. When empty, it is on from {@link Level#STRICT}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictNames() {
        return Optional.ofNullable(strictNames);
    }

    /**
     * Returns whether ARM locations are stored canonical, as Azure stores them: a PUT or PATCH body's
     * {@code location} is normalized for the subscription's cloud ({@code "East US"} becomes {@code eastus})
     * before its handler runs, a type that inherits its group's location gets it when the body names none,
     * and a global type gets {@code global}. When empty, it is on from {@link Level#AZURE}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getLocationNormalize() {
        return Optional.ofNullable(locationNormalize);
    }

    /**
     * Returns whether a resource PUT naming a location its subscription's cloud does not offer is refused
     * with {@code 400 NoRegisteredProviderFound}. When empty, it is on from {@link Level#STRICT}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getStrictLocations() {
        return Optional.ofNullable(strictLocations);
    }

    /**
     * Returns whether ARM answers as the caller's tenant: the (unverified) {@code tid} of a Bearer token
     * picks the tenant, {@code GET /subscriptions} lists that tenant's subscriptions, and a subscription
     * seen for the first time is registered under it. When empty, it is on from {@link Level#AZURE}.
     *
     * @return the override, or {@link Optional#empty()} if it follows the level
     */
    public Optional<Boolean> getTenantScoping() {
        return Optional.ofNullable(tenantScoping);
    }

    /**
     * Applies the parity environment variables to the given container. Overrides are only emitted when
     * set, so that they follow the level otherwise.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_PARITY", level.name().toLowerCase(Locale.ROOT));

        applyOverride(container, "STRICT_TOKENS", strictTokens);
        applyOverride(container, "SUBSCRIPTION_LISTING", subscriptionListing);
        applyOverride(container, "STRICT_SUBSCRIPTIONS", strictSubscriptions);
        applyOverride(container, "RESOURCE_GROUP_SEMANTICS", resourceGroupSemantics);
        applyOverride(container, "RESOURCE_GROUP_DELETE", resourceGroupDelete);
        applyOverride(container, "STRICT_RESOURCE_GROUPS", strictResourceGroups);
        applyOverride(container, "STRICT_PROVIDERS", strictProviders);
        applyOverride(container, "STRICT_NAMES", strictNames);
        applyOverride(container, "LOCATION_NORMALIZE", locationNormalize);
        applyOverride(container, "STRICT_LOCATIONS", strictLocations);
        applyOverride(container, "TENANT_SCOPING", tenantScoping);
    }

    private static void applyOverride(Container<?> container, String name, Boolean value) {
        if (value != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_" + name, String.valueOf(value));
        }
    }

    /**
     * Builder for {@link ParityConfig}.
     */
    public static class Builder {

        private Level level = DEFAULT_LEVEL;
        private Boolean strictTokens;
        private Boolean subscriptionListing;
        private Boolean strictSubscriptions;
        private Boolean resourceGroupSemantics;
        private Boolean resourceGroupDelete;
        private Boolean strictResourceGroups;
        private Boolean strictProviders;
        private Boolean strictNames;
        private Boolean locationNormalize;
        private Boolean strictLocations;
        private Boolean tenantScoping;

        private Builder() {
            // Allow instantiation only via ParityConfig.builder()
        }

        private Builder(ParityConfig instance) {
            this.level = instance.level;
            this.strictTokens = instance.strictTokens;
            this.subscriptionListing = instance.subscriptionListing;
            this.strictSubscriptions = instance.strictSubscriptions;
            this.resourceGroupSemantics = instance.resourceGroupSemantics;
            this.resourceGroupDelete = instance.resourceGroupDelete;
            this.strictResourceGroups = instance.strictResourceGroups;
            this.strictProviders = instance.strictProviders;
            this.strictNames = instance.strictNames;
            this.locationNormalize = instance.locationNormalize;
            this.strictLocations = instance.strictLocations;
            this.tenantScoping = instance.tenantScoping;
        }

        /**
         * Sets the Azure parity level, which every override defaults from.
         *
         * @param level the parity level (default {@code COMPAT})
         * @return this builder
         */
        public Builder level(Level level) {
            this.level = Objects.requireNonNull(level, "level");
            return this;
        }

        /**
         * Sets whether an ARM request whose Bearer token is missing, not signed by the Entra signing key,
         * expired, or issued for another audience is refused with {@code 401}. When not set, it is on from
         * {@link Level#STRICT} or when Entra's {@code validateTokens} is set; this override wins over both.
         *
         * @param strictTokens the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictTokens(Boolean strictTokens) {
            this.strictTokens = strictTokens;
            return this;
        }

        /**
         * Sets whether {@code GET /subscriptions} also lists subscriptions registered on first use, and
         * {@code GET /subscriptions/{sub}} echoes the directory's id rather than the path's spelling. When
         * not set, it is on from {@link Level#AZURE}.
         *
         * @param subscriptionListing the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder subscriptionListing(Boolean subscriptionListing) {
            this.subscriptionListing = subscriptionListing;
            return this;
        }

        /**
         * Sets whether an ARM request naming a subscription the directory does not hold is refused with
         * {@code 404 SubscriptionNotFound} instead of registering it on first use. When not set, it is on
         * from {@link Level#STRICT}.
         *
         * @param strictSubscriptions the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictSubscriptions(Boolean strictSubscriptions) {
            this.strictSubscriptions = strictSubscriptions;
            return this;
        }

        /**
         * Sets whether resource groups behave as Azure's: a re-PUT answers {@code 200} rather than
         * {@code 201}, a {@code GET} of a missing group is {@code 404 ResourceGroupNotFound}, a child PUT
         * under a missing group creates the group, and groups are kept in the directory store. When not
         * set, it is on from {@link Level#AZURE}.
         *
         * @param resourceGroupSemantics the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder resourceGroupSemantics(Boolean resourceGroupSemantics) {
            this.resourceGroupSemantics = resourceGroupSemantics;
            return this;
        }

        /**
         * Sets whether a resource-group {@code DELETE} is Azure's long-running operation: {@code 202} with
         * a {@code Location} to poll, every resource in the group deleted first, and
         * {@code 404 ResourceGroupNotFound} for a missing group. When not set, it is on from
         * {@link Level#AZURE}.
         *
         * @param resourceGroupDelete the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder resourceGroupDelete(Boolean resourceGroupDelete) {
            this.resourceGroupDelete = resourceGroupDelete;
            return this;
        }

        /**
         * Sets whether a request for a resource under a resource group that does not exist (a PUT, a
         * read, an action or a delete) is refused with {@code 404 ResourceGroupNotFound}. When not set, it
         * is on from {@link Level#STRICT}.
         *
         * @param strictResourceGroups the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictResourceGroups(Boolean strictResourceGroups) {
            this.strictResourceGroups = strictResourceGroups;
            return this;
        }

        /**
         * Sets whether resource providers start {@code NotRegistered} in each subscription, so a request
         * for a resource type of an unregistered namespace is refused with
         * {@code 409 MissingSubscriptionRegistration} until the namespace is registered. When not set, it
         * is on from {@link Level#STRICT}.
         *
         * @param strictProviders the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictProviders(Boolean strictProviders) {
            this.strictProviders = strictProviders;
            return this;
        }

        /**
         * Sets whether a PUT whose resource name breaks its type's naming rule is refused with {@code 400}
         * before its handler runs. When not set, it is on from {@link Level#STRICT}.
         *
         * @param strictNames the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictNames(Boolean strictNames) {
            this.strictNames = strictNames;
            return this;
        }

        /**
         * Sets whether ARM locations are stored canonical, as Azure stores them: a PUT or PATCH body's
         * {@code location} is normalized for the subscription's cloud ({@code "East US"} becomes
         * {@code eastus}) before its handler runs, a type that inherits its group's location gets it when
         * the body names none, and a global type gets {@code global}. When not set, it is on from
         * {@link Level#AZURE}.
         *
         * @param locationNormalize the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder locationNormalize(Boolean locationNormalize) {
            this.locationNormalize = locationNormalize;
            return this;
        }

        /**
         * Sets whether a resource PUT naming a location its subscription's cloud does not offer is refused
         * with {@code 400 NoRegisteredProviderFound}. When not set, it is on from {@link Level#STRICT}.
         *
         * @param strictLocations the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder strictLocations(Boolean strictLocations) {
            this.strictLocations = strictLocations;
            return this;
        }

        /**
         * Sets whether ARM answers as the caller's tenant: the (unverified) {@code tid} of a Bearer token
         * picks the tenant, {@code GET /subscriptions} lists that tenant's subscriptions (the default
         * subscription when it owns none), and a subscription seen for the first time is registered under
         * it. {@code GET /tenants} keeps listing every tenant. When not set, it is on from
         * {@link Level#AZURE}.
         *
         * @param tenantScoping the override ({@code null} to follow the level)
         * @return this builder
         */
        public Builder tenantScoping(Boolean tenantScoping) {
            this.tenantScoping = tenantScoping;
            return this;
        }

        /**
         * Creates an immutable {@link ParityConfig} from this builder.
         *
         * @return the parity configuration
         */
        public ParityConfig build() {
            return new ParityConfig(this);
        }
    }
}

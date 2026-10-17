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
    private final Boolean subscriptionListing;
    private final Boolean strictSubscriptions;
    private final Boolean resourceGroupSemantics;
    private final Boolean resourceGroupDelete;
    private final Boolean strictResourceGroups;

    private ParityConfig(Builder builder) {
        this.level = builder.level;
        this.subscriptionListing = builder.subscriptionListing;
        this.strictSubscriptions = builder.strictSubscriptions;
        this.resourceGroupSemantics = builder.resourceGroupSemantics;
        this.resourceGroupDelete = builder.resourceGroupDelete;
        this.strictResourceGroups = builder.strictResourceGroups;
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
     * Applies the parity environment variables to the given container. Overrides are only emitted when
     * set, so that they follow the level otherwise.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_PARITY", level.name().toLowerCase(Locale.ROOT));

        if (subscriptionListing != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_SUBSCRIPTION_LISTING", String.valueOf(subscriptionListing));
        }

        if (strictSubscriptions != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_STRICT_SUBSCRIPTIONS", String.valueOf(strictSubscriptions));
        }

        if (resourceGroupSemantics != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_SEMANTICS", String.valueOf(resourceGroupSemantics));
        }

        if (resourceGroupDelete != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_RESOURCE_GROUP_DELETE", String.valueOf(resourceGroupDelete));
        }

        if (strictResourceGroups != null) {
            container.withEnv("FLOCI_AZ_PARITY_OVERRIDES_STRICT_RESOURCE_GROUPS", String.valueOf(strictResourceGroups));
        }
    }

    /**
     * Builder for {@link ParityConfig}.
     */
    public static class Builder {

        private Level level = DEFAULT_LEVEL;
        private Boolean subscriptionListing;
        private Boolean strictSubscriptions;
        private Boolean resourceGroupSemantics;
        private Boolean resourceGroupDelete;
        private Boolean strictResourceGroups;

        private Builder() {
            // Allow instantiation only via ParityConfig.builder()
        }

        private Builder(ParityConfig instance) {
            this.level = instance.level;
            this.subscriptionListing = instance.subscriptionListing;
            this.strictSubscriptions = instance.strictSubscriptions;
            this.resourceGroupSemantics = instance.resourceGroupSemantics;
            this.resourceGroupDelete = instance.resourceGroupDelete;
            this.strictResourceGroups = instance.strictResourceGroups;
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
         * Creates an immutable {@link ParityConfig} from this builder.
         *
         * @return the parity configuration
         */
        public ParityConfig build() {
            return new ParityConfig(this);
        }
    }
}

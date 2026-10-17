package io.floci.testcontainers.az.config;

import java.util.Objects;
import java.util.Optional;

/**
 * One subscription of the Floci Azure directory, see {@link DirectoryConfig}. Only the id is required;
 * the other properties fall back to Floci Azure's defaults.
 *
 * <p>Instances are created via {@link DirectoryConfig.Builder}, e.g.:
 * <pre>{@code
 * DirectoryConfig config = DirectoryConfig.builder()
 *     .subscription("22222222-0000-0000-0000-000000000000", s -> s
 *         .tenantId("11111111-0000-0000-0000-000000000000")
 *         .displayName("contoso-dev")
 *         .state("Enabled"))
 *     .build();
 * }</pre>
 */
public final class SubscriptionConfig {

    private final String id;
    private final String tenantId;
    private final String displayName;
    private final String state;

    private SubscriptionConfig(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.displayName = builder.displayName;
        this.state = builder.state;
    }

    static Builder builder(String id) {
        return new Builder(id);
    }

    /**
     * Returns the subscription id.
     *
     * @return the subscription id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the id of the tenant owning the subscription. When empty, the subscription belongs to the
     * default tenant (the Entra ID {@code defaultTenantId}).
     *
     * @return the tenant id, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getTenantId() {
        return Optional.ofNullable(tenantId);
    }

    /**
     * Returns the display name of the subscription. When empty, Floci Azure uses {@code floci-az local}.
     *
     * @return the display name, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns the state of the subscription ({@code Enabled}, {@code Warned}, {@code PastDue},
     * {@code Disabled} or {@code Deleted}). When empty, the subscription is {@code Enabled}.
     *
     * @return the state, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getState() {
        return Optional.ofNullable(state);
    }

    /**
     * Builder for {@link SubscriptionConfig}.
     */
    public static final class Builder {

        private final String id;
        private String tenantId;
        private String displayName;
        private String state;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        /**
         * Sets the id of the tenant owning the subscription, which must be the default tenant or a
         * {@linkplain DirectoryConfig.Builder#tenant(String, java.util.function.Consumer) configured one}.
         * When not set, the subscription belongs to the default tenant.
         *
         * @param tenantId the tenant id ({@code null} to unset)
         * @return this builder
         */
        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        /**
         * Sets the display name of the subscription. When not set, Floci Azure uses {@code floci-az local}.
         *
         * @param displayName the display name ({@code null} to unset)
         * @return this builder
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the state of the subscription: {@code Enabled}, {@code Warned}, {@code PastDue},
         * {@code Disabled} or {@code Deleted}. An unknown state makes Floci Azure fail at startup. When
         * not set, the subscription is {@code Enabled}.
         *
         * @param state the state ({@code null} to unset)
         * @return this builder
         */
        public Builder state(String state) {
            this.state = state;
            return this;
        }

        SubscriptionConfig build() {
            return new SubscriptionConfig(this);
        }
    }
}

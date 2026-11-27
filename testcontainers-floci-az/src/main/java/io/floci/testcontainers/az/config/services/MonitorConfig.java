package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Monitor / Log Analytics of Floci Azure.
 *
 * <p>Covers Microsoft.OperationalInsights and Microsoft.Insights resources, including the subscription
 * Activity Log ({@code Microsoft.Insights/eventtypes/management/values}).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * MonitorConfig config = MonitorConfig.builder()
 *     .activityLogCapacity(500)
 *     .activityLogPageSize(50)
 *     .build();
 * }</pre>
 */
public class MonitorConfig extends AbstractServiceConfig<MonitorConfig.Builder> {

    private static final boolean DEFAULT_ACTIVITY_LOG_ENABLED = true;
    private static final int DEFAULT_ACTIVITY_LOG_CAPACITY = 10000;
    private static final int DEFAULT_ACTIVITY_LOG_PAGE_SIZE = 1000;

    private final boolean activityLogEnabled;
    private final int activityLogCapacity;
    private final int activityLogPageSize;

    private MonitorConfig(Builder builder) {
        super(builder);
        this.activityLogEnabled = builder.activityLogEnabled;
        this.activityLogCapacity = builder.activityLogCapacity;
        this.activityLogPageSize = builder.activityLogPageSize;
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
     * Returns whether the subscription Activity Log records an {@code Administrative} entry for every
     * management-plane write, delete and action and serves the list operation.
     *
     * @return {@code true} if the Activity Log is enabled
     */
    public boolean isActivityLogEnabled() {
        return activityLogEnabled;
    }

    /**
     * Returns the number of Activity Log entries kept in memory; the oldest are dropped beyond it.
     *
     * @return the Activity Log capacity
     */
    public int getActivityLogCapacity() {
        return activityLogCapacity;
    }

    /**
     * Returns the number of Activity Log entries per page of the list operation; the next page follows
     * the {@code nextLink}.
     *
     * @return the Activity Log page size
     */
    public int getActivityLogPageSize() {
        return activityLogPageSize;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_MONITOR_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_ENABLED", String.valueOf(activityLogEnabled));
            container.withEnv("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_CAPACITY", String.valueOf(activityLogCapacity));
            container.withEnv("FLOCI_AZ_SERVICES_MONITOR_ACTIVITY_LOG_PAGE_SIZE", String.valueOf(activityLogPageSize));
        }
    }

    /**
     * Builder for {@link MonitorConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, MonitorConfig> {

        private boolean activityLogEnabled = DEFAULT_ACTIVITY_LOG_ENABLED;
        private int activityLogCapacity = DEFAULT_ACTIVITY_LOG_CAPACITY;
        private int activityLogPageSize = DEFAULT_ACTIVITY_LOG_PAGE_SIZE;

        private Builder() {
            // Allow instantiation only via MonitorConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link MonitorConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(MonitorConfig instance) {
            super(instance);
            this.activityLogEnabled = instance.activityLogEnabled;
            this.activityLogCapacity = instance.activityLogCapacity;
            this.activityLogPageSize = instance.activityLogPageSize;
        }

        /**
         * Sets whether the subscription Activity Log records an {@code Administrative} entry for every
         * management-plane write, delete and action and serves the list operation.
         *
         * @param activityLogEnabled {@code true} to enable the Activity Log (default {@value DEFAULT_ACTIVITY_LOG_ENABLED})
         * @return this builder
         */
        public Builder activityLogEnabled(boolean activityLogEnabled) {
            this.activityLogEnabled = activityLogEnabled;
            return this;
        }

        /**
         * Sets the number of Activity Log entries kept in memory; the oldest are dropped beyond it.
         *
         * @param activityLogCapacity the Activity Log capacity (default {@value DEFAULT_ACTIVITY_LOG_CAPACITY})
         * @return this builder
         */
        public Builder activityLogCapacity(int activityLogCapacity) {
            this.activityLogCapacity = activityLogCapacity;
            return this;
        }

        /**
         * Sets the number of Activity Log entries per page of the list operation; the next page follows
         * the {@code nextLink}.
         *
         * @param activityLogPageSize the Activity Log page size (default {@value DEFAULT_ACTIVITY_LOG_PAGE_SIZE})
         * @return this builder
         */
        public Builder activityLogPageSize(int activityLogPageSize) {
            this.activityLogPageSize = activityLogPageSize;
            return this;
        }

        /**
         * Creates an immutable {@link MonitorConfig} from this builder.
         *
         * @return the Monitor configuration
         */
        @Override
        public MonitorConfig build() {
            return new MonitorConfig(this);
        }
    }
}

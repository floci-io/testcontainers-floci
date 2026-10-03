package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Redshift Data API-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedshiftDataConfig config = RedshiftDataConfig.builder()
 *     .resultTtlHours(1)
 *     .build();
 * }</pre>
 */
public class RedshiftDataConfig extends AbstractServiceConfig<RedshiftDataConfig.Builder> {

    private static final int DEFAULT_RESULT_TTL_HOURS = 24;

    private final int resultTtlHours;

    private RedshiftDataConfig(Builder builder) {
        super(builder.enabled);
        this.resultTtlHours = builder.resultTtlHours;
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
     * Returns the number of hours statement results are kept for DescribeStatement and GetStatementResult.
     *
     * @return the number of hours statement results are kept for DescribeStatement and GetStatementResult
     */
    public int getResultTtlHours() {
        return resultTtlHours;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_REDSHIFT_DATA_RESULT_TTL_HOURS", String.valueOf(resultTtlHours));
        }
    }

    /**
     * Builder for {@link RedshiftDataConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedshiftDataConfig> {

        private int resultTtlHours = DEFAULT_RESULT_TTL_HOURS;

        private Builder() {
            // Allow instantiation only via RedshiftDataConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedshiftDataConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedshiftDataConfig instance) {
            super(instance);
            this.resultTtlHours = instance.getResultTtlHours();
        }

        /**
         * Sets the number of hours statement results are kept for DescribeStatement and GetStatementResult.
         *
         * @param resultTtlHours the number of hours statement results are kept for DescribeStatement and GetStatementResult (default {@value DEFAULT_RESULT_TTL_HOURS})
         * @return this builder
         */
        public Builder resultTtlHours(int resultTtlHours) {
            this.resultTtlHours = resultTtlHours;
            return this;
        }

        /**
         * Creates an immutable {@link RedshiftDataConfig} from this builder.
         *
         * @return the Redshift Data API configuration
         */
        @Override
        public RedshiftDataConfig build() {
            return new RedshiftDataConfig(this);
        }
    }
}

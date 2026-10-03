package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Glue-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * GlueConfig config = GlueConfig.builder()
 *     .build();
 * }</pre>
 */
public class GlueConfig extends AbstractServiceConfig<GlueConfig.Builder> {

    private static final int DEFAULT_JOB_RUN_DURATION_SECONDS = 0;
    private static final int DEFAULT_CRAWLER_RUN_DURATION_SECONDS = 0;

    private final int jobRunDurationSeconds;
    private final int crawlerRunDurationSeconds;

    private GlueConfig(Builder builder) {
        super(builder.enabled);
        this.jobRunDurationSeconds = builder.jobRunDurationSeconds;
        this.crawlerRunDurationSeconds = builder.crawlerRunDurationSeconds;
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
     * Returns how long, in seconds, a job run stays {@code RUNNING} before it succeeds.
     *
     * <p>{@code 0} means a run succeeds as soon as it starts.
     *
     * @return how long, in seconds, a job run stays {@code RUNNING} before it succeeds
     */
    public int getJobRunDurationSeconds() {
        return jobRunDurationSeconds;
    }

    /**
     * Returns how long, in seconds, a crawl keeps the crawler {@code RUNNING} before it succeeds.
     *
     * <p>{@code 0} means a crawl finishes as soon as it starts.
     *
     * @return how long, in seconds, a crawl keeps the crawler {@code RUNNING} before it succeeds
     */
    public int getCrawlerRunDurationSeconds() {
        return crawlerRunDurationSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_GLUE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_GLUE_JOB_RUN_DURATION_SECONDS", String.valueOf(jobRunDurationSeconds));
            container.withEnv("FLOCI_SERVICES_GLUE_CRAWLER_RUN_DURATION_SECONDS", String.valueOf(crawlerRunDurationSeconds));
        }
    }

    /**
     * Builder for {@link GlueConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, GlueConfig> {

        private int jobRunDurationSeconds = DEFAULT_JOB_RUN_DURATION_SECONDS;
        private int crawlerRunDurationSeconds = DEFAULT_CRAWLER_RUN_DURATION_SECONDS;

        private Builder() {
            // Allow instantiation only via GlueConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link GlueConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(GlueConfig instance) {
            super(instance);
            this.jobRunDurationSeconds = instance.getJobRunDurationSeconds();
            this.crawlerRunDurationSeconds = instance.getCrawlerRunDurationSeconds();
        }

        /**
         * Sets how long, in seconds, a job run stays {@code RUNNING} before it succeeds.
         *
         * <p>{@code 0} means a run succeeds as soon as it starts.
         *
         * @param jobRunDurationSeconds how long, in seconds, a job run stays {@code RUNNING} before it succeeds (default {@value DEFAULT_JOB_RUN_DURATION_SECONDS})
         * @return this builder
         */
        public Builder jobRunDurationSeconds(int jobRunDurationSeconds) {
            this.jobRunDurationSeconds = jobRunDurationSeconds;
            return this;
        }

        /**
         * Sets how long, in seconds, a crawl keeps the crawler {@code RUNNING} before it succeeds.
         *
         * <p>{@code 0} means a crawl finishes as soon as it starts.
         *
         * @param crawlerRunDurationSeconds how long, in seconds, a crawl keeps the crawler {@code RUNNING} before it succeeds (default {@value DEFAULT_CRAWLER_RUN_DURATION_SECONDS})
         * @return this builder
         */
        public Builder crawlerRunDurationSeconds(int crawlerRunDurationSeconds) {
            this.crawlerRunDurationSeconds = crawlerRunDurationSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link GlueConfig} from this builder.
         *
         * @return the Glue configuration
         */
        @Override
        public GlueConfig build() {
            return new GlueConfig(this);
        }
    }
}

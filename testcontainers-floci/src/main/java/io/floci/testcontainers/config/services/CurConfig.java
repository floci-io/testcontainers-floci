package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CUR (Cost and Usage Reports)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CurConfig config = CurConfig.builder()
 *     .emitMode("daily")
 *     .stagingBucket("my-cur-staging")
 *     .build();
 * }</pre>
 */
public class CurConfig extends AbstractServiceConfig<CurConfig.Builder> {

    private static final String DEFAULT_EMIT_MODE = "synchronous";
    private static final String DEFAULT_STAGING_BUCKET = "floci-cur-staging";

    private final String emitMode;
    private final String stagingBucket;

    private CurConfig(Builder builder) {
        super(builder.enabled);
        this.emitMode = builder.emitMode;
        this.stagingBucket = builder.stagingBucket;
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
     * Returns the emit mode controlling when CUR Parquet artifacts are produced.
     *
     * <ul>
     *   <li>{@code synchronous} — emit on report definition mutations (default; suits tests)</li>
     *   <li>{@code daily} — emit once per 24h via the CUR-owned scheduled executor</li>
     *   <li>{@code off} — management plane only, no Parquet emission</li>
     * </ul>
     *
     * @return the emit mode (default {@value DEFAULT_EMIT_MODE})
     */
    public String getEmitMode() {
        return emitMode;
    }

    /**
     * Returns the S3 bucket used to stage NDJSON row payloads before DuckDB writes the final
     * Parquet artifact.
     *
     * @return the staging bucket name (default {@value DEFAULT_STAGING_BUCKET})
     */
    public String getStagingBucket() {
        return stagingBucket;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CUR_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CUR_EMIT_MODE", emitMode);
            container.withEnv("FLOCI_SERVICES_CUR_STAGING_BUCKET", stagingBucket);
        }
    }

    /**
     * Builder for {@link CurConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CurConfig> {

        private String emitMode = DEFAULT_EMIT_MODE;
        private String stagingBucket = DEFAULT_STAGING_BUCKET;

        private Builder() {
            // Allow instantiation only via CurConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CurConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CurConfig instance) {
            super(instance);
            this.emitMode = instance.getEmitMode();
            this.stagingBucket = instance.getStagingBucket();
        }

        /**
         * Sets the emit mode controlling when CUR Parquet artifacts are produced.
         *
         * @param emitMode {@code synchronous}, {@code daily}, or {@code off}
         *                 (default {@value DEFAULT_EMIT_MODE})
         * @return this builder
         */
        public Builder emitMode(String emitMode) {
            this.emitMode = emitMode;
            return this;
        }

        /**
         * Sets the S3 bucket used to stage NDJSON row payloads before DuckDB writes the final
         * Parquet artifact. Created on first use if it doesn't exist.
         *
         * @param stagingBucket the bucket name (default {@value DEFAULT_STAGING_BUCKET})
         * @return this builder
         */
        public Builder stagingBucket(String stagingBucket) {
            this.stagingBucket = stagingBucket;
            return this;
        }

        /**
         * Creates an immutable {@link CurConfig} from this builder.
         *
         * @return the CUR configuration
         */
        public CurConfig build() {
            return new CurConfig(this);
        }
    }
}

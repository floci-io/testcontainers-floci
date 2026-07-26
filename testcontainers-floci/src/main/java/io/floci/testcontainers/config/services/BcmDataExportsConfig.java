package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for BCM Data Exports-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * BcmDataExportsConfig config = BcmDataExportsConfig.builder()
 *     .emitMode("daily")
 *     .build();
 * }</pre>
 */
public class BcmDataExportsConfig extends AbstractServiceConfig<BcmDataExportsConfig.Builder> {

    private static final String DEFAULT_EMIT_MODE = "synchronous";

    private final String emitMode;

    private BcmDataExportsConfig(Builder builder) {
        super(builder.enabled);
        this.emitMode = builder.emitMode;
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
     * Returns the emit mode controlling when BCM Data Exports {@code Export} Parquet artifacts
     * are produced.
     *
     * <ul>
     *   <li>{@code synchronous} — emit on export mutations (default; suits tests)</li>
     *   <li>{@code daily} — emit once per 24h via the scheduled executor</li>
     *   <li>{@code off} — management plane only, no Parquet emission</li>
     * </ul>
     *
     * @return the emit mode (default {@value DEFAULT_EMIT_MODE})
     */
    public String getEmitMode() {
        return emitMode;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_BCM_DATA_EXPORTS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_BCM_DATA_EXPORTS_EMIT_MODE", emitMode);
        }
    }

    /**
     * Builder for {@link BcmDataExportsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, BcmDataExportsConfig> {

        private String emitMode = DEFAULT_EMIT_MODE;

        private Builder() {
            // Allow instantiation only via BcmDataExportsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link BcmDataExportsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(BcmDataExportsConfig instance) {
            super(instance);
            this.emitMode = instance.getEmitMode();
        }

        /**
         * Sets the emit mode controlling when BCM Data Exports Parquet artifacts are produced.
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
         * Creates an immutable {@link BcmDataExportsConfig} from this builder.
         *
         * @return the BCM Data Exports configuration
         */
        public BcmDataExportsConfig build() {
            return new BcmDataExportsConfig(this);
        }
    }
}

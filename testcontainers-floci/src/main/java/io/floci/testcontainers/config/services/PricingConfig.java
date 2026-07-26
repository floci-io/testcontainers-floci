package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Pricing-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PricingConfig config = PricingConfig.builder()
 *     .build();
 * }</pre>
 */
public class PricingConfig extends AbstractServiceConfig<PricingConfig.Builder> {

    private final String snapshotPath;

    private PricingConfig(Builder builder) {
        super(builder.enabled);
        this.snapshotPath = builder.snapshotPath;
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
     * Returns the filesystem directory that overrides the bundled pricing snapshot, or
     * {@link Optional#empty()} if the built-in snapshot is used.
     *
     * <p>When set, files at {@code <path>/services.json},
     * {@code <path>/products/<service>/<region>.json},
     * {@code <path>/attribute-values/<service>/<attribute>.json}, and
     * {@code <path>/price-lists/<service>.json} are read in preference to the classpath copy.
     *
     * @return the snapshot path, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getSnapshotPath() {
        return Optional.ofNullable(snapshotPath);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_PRICING_ENABLED", String.valueOf(isEnabled()));
        if (snapshotPath != null) {
            container.withEnv("FLOCI_SERVICES_PRICING_SNAPSHOT_PATH", snapshotPath);
        }
    }

    /**
     * Builder for {@link PricingConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, PricingConfig> {

        private String snapshotPath = null;

        private Builder() {
            // Allow instantiation only via PricingConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link PricingConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(PricingConfig instance) {
            super(instance);
            this.snapshotPath = instance.getSnapshotPath().orElse(null);
        }

        /**
         * Sets the filesystem directory that overrides the bundled pricing snapshot.
         *
         * <p>When set, files at {@code <path>/services.json},
         * {@code <path>/products/<service>/<region>.json},
         * {@code <path>/attribute-values/<service>/<attribute>.json}, and
         * {@code <path>/price-lists/<service>.json} are read in preference to the classpath copy.
         *
         * @param snapshotPath the host path to the snapshot directory, or {@code null} to unset
         * @return this builder
         */
        public Builder snapshotPath(String snapshotPath) {
            this.snapshotPath = snapshotPath;
            return this;
        }

        /**
         * Creates an immutable {@link PricingConfig} from this builder.
         *
         * @return the Pricing configuration
         */
        public PricingConfig build() {
            return new PricingConfig(this);
        }
    }
}

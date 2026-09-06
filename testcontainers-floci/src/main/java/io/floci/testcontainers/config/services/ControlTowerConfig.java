package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Control Tower-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ControlTowerConfig config = ControlTowerConfig.builder()
 *     .seedLandingZone(true)
 *     .build();
 * }</pre>
 */
public class ControlTowerConfig extends AbstractServiceConfig<ControlTowerConfig.Builder> {

    /** Default value for the {@link #hasSeedLandingZone()} flag. */
    private static final boolean DEFAULT_SEED_LANDING_ZONE = false;

    private final boolean seedLandingZone;

    private ControlTowerConfig(Builder builder) {
        super(builder.enabled);
        this.seedLandingZone = builder.seedLandingZone;
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
     * Returns whether a landing zone should be seeded at startup.
     *
     * <p>Floci's normal runtime starts with no landing zone, so {@code ListLandingZones} returns
     * an empty list until {@code CreateLandingZone} is called. Enabling this restores the
     * deterministic seeded landing-zone fixture.
     *
     * @return {@code true} if a landing zone should be seeded
     */
    public boolean hasSeedLandingZone() {
        return seedLandingZone;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CONTROLTOWER_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CONTROLTOWER_SEED_LANDING_ZONE", String.valueOf(seedLandingZone));
        }
    }

    /**
     * Builder for {@link ControlTowerConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ControlTowerConfig> {

        private boolean seedLandingZone = DEFAULT_SEED_LANDING_ZONE;

        private Builder() {
            // Allow instantiation only via ControlTowerConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ControlTowerConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ControlTowerConfig instance) {
            super(instance);
            this.seedLandingZone = instance.hasSeedLandingZone();
        }

        /**
         * Enables or disables seeding a landing zone at startup.
         *
         * @param seedLandingZone {@code true} to seed a landing zone (default {@value DEFAULT_SEED_LANDING_ZONE})
         * @return this builder
         */
        public Builder seedLandingZone(boolean seedLandingZone) {
            this.seedLandingZone = seedLandingZone;
            return this;
        }

        /**
         * Creates an immutable {@link ControlTowerConfig} from this builder.
         *
         * @return the Control Tower configuration
         */
        @Override
        public ControlTowerConfig build() {
            return new ControlTowerConfig(this);
        }
    }
}

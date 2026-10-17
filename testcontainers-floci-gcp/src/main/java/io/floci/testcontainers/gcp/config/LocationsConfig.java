package io.floci.testcontainers.gcp.config;

import org.testcontainers.containers.Container;

/**
 * Configuration of the GCP location catalog of Floci GCP.
 *
 * <p>Floci GCP bundles a catalog of real GCP regions and zones. It serves the {@code google.cloud.location.Locations}
 * API of every regional service and Compute Engine's regions and zones from it. By default Floci GCP still
 * accepts any location string on regional calls; with {@link Builder#strict(boolean) strict} validation, unknown
 * locations are rejected.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * LocationsConfig config = LocationsConfig.builder()
 *     .strict(true)
 *     .build();
 * }</pre>
 */
public class LocationsConfig {

    private static final boolean DEFAULT_STRICT = false;

    private final boolean strict;

    private LocationsConfig(Builder builder) {
        this.strict = builder.strict;
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
     * Returns whether locations that are not in Floci GCP's bundled location catalog (and location combinations
     * that cannot exist) are rejected with {@code INVALID_ARGUMENT} on regional create and list calls.
     *
     * @return {@code true} if locations are validated strictly
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Applies this locations configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_LOCATIONS_STRICT", String.valueOf(strict));
    }

    /**
     * Builder for {@link LocationsConfig}.
     */
    public static class Builder {

        private boolean strict = DEFAULT_STRICT;

        private Builder() {
            // Allow instantiation only via LocationsConfig.builder()
        }

        private Builder(LocationsConfig instance) {
            this.strict = instance.strict;
        }

        /**
         * Sets whether locations that are not in Floci GCP's bundled location catalog are rejected with
         * {@code INVALID_ARGUMENT} on regional create and list calls (e.g. of Cloud Run, Cloud Functions, KMS,
         * Cloud Tasks, Cloud Scheduler, Eventarc, Managed Kafka and GKE), as are location combinations that cannot
         * exist, such as a Cloud SQL zone outside the instance region. The {@code -} list wildcard is never
         * checked. Compute Engine does not use this setting.
         *
         * @param strict {@code true} to validate locations strictly (default {@value DEFAULT_STRICT})
         * @return this builder
         */
        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * Creates an immutable {@link LocationsConfig} from this builder.
         *
         * @return the locations configuration
         */
        public LocationsConfig build() {
            return new LocationsConfig(this);
        }
    }
}

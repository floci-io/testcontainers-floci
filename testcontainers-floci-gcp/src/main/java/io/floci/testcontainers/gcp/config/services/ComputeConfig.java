package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.List;
import java.util.Optional;

/**
 * Configuration for Compute Engine of Floci GCP.
 *
 * <p>Compute Engine (instances, networks, disks and other {@code compute.googleapis.com} resources) is emulated
 * as control plane only; no virtual machines are started.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ComputeConfig config = ComputeConfig.builder()
 *     .operationDelayMs(0)
 *     .regions(List.of("us-central1", "europe-west3"))
 *     .build();
 * }</pre>
 */
public class ComputeConfig extends AbstractServiceConfig<ComputeConfig.Builder> {

    private static final long DEFAULT_OPERATION_DELAY_MS = 50;

    private final long operationDelayMs;
    private final List<String> regions;

    private ComputeConfig(Builder builder) {
        super(builder);
        this.operationDelayMs = builder.operationDelayMs;
        this.regions = builder.regions == null ? null : List.copyOf(builder.regions);
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
     * Returns the time in milliseconds until a long-running Compute Engine operation is reported as
     * {@code DONE}.
     *
     * @return the operation delay in milliseconds
     */
    public long getOperationDelayMs() {
        return operationDelayMs;
    }

    /**
     * Returns the allow-list of regions Floci GCP offers for Compute Engine. Each listed region keeps its real
     * zones from Floci GCP's bundled location catalog; requests for other regions or zones are rejected as not
     * found. When empty, Floci GCP serves all regions of its location catalog.
     *
     * @return the regions, or empty if not set
     */
    public Optional<List<String>> getRegions() {
        return Optional.ofNullable(regions);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", String.valueOf(operationDelayMs));
            if (regions != null) {
                container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", String.join(",", regions));
            }
        }
    }

    /**
     * Builder for {@link ComputeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ComputeConfig> {

        private long operationDelayMs = DEFAULT_OPERATION_DELAY_MS;
        private List<String> regions;

        private Builder() {
            // Allow instantiation only via ComputeConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ComputeConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ComputeConfig instance) {
            super(instance);
            this.operationDelayMs = instance.operationDelayMs;
            this.regions = instance.regions;
        }

        /**
         * Sets the time in milliseconds until a long-running Compute Engine operation is reported as
         * {@code DONE}. Floci GCP uses at least 2 ms.
         *
         * @param operationDelayMs the operation delay in milliseconds (default {@value DEFAULT_OPERATION_DELAY_MS})
         * @return this builder
         */
        public Builder operationDelayMs(long operationDelayMs) {
            this.operationDelayMs = operationDelayMs;
            return this;
        }

        /**
         * Sets an allow-list of regions Floci GCP offers for Compute Engine. Each listed region keeps its real
         * zones from Floci GCP's bundled location catalog; requests for other regions or zones are rejected as
         * not found. Regions unknown to the catalog are ignored by Floci GCP. When not set (or empty), Floci GCP
         * serves all regions of its location catalog.
         *
         * @param regions the regions, or {@code null} to serve the whole location catalog (default)
         * @return this builder
         */
        public Builder regions(List<String> regions) {
            this.regions = regions;
            return this;
        }

        /**
         * Creates an immutable {@link ComputeConfig} from this builder.
         *
         * @return the Compute Engine configuration
         */
        @Override
        public ComputeConfig build() {
            return new ComputeConfig(this);
        }
    }
}

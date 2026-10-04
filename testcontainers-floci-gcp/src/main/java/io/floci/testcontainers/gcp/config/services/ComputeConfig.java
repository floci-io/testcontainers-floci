package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.List;

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
    private static final List<String> DEFAULT_REGIONS = List.of("us-central1", "europe-west1");

    private final long operationDelayMs;
    private final List<String> regions;

    private ComputeConfig(Builder builder) {
        super(builder);
        this.operationDelayMs = builder.operationDelayMs;
        this.regions = List.copyOf(builder.regions);
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
     * Returns the regions Floci GCP offers for Compute Engine. Each region has the zones {@code <region>-a},
     * {@code -b} and {@code -c}; requests for other regions or zones are rejected as not found.
     *
     * @return the regions
     */
    public List<String> getRegions() {
        return regions;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", String.valueOf(operationDelayMs));
            container.withEnv("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", String.join(",", regions));
        }
    }

    /**
     * Builder for {@link ComputeConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ComputeConfig> {

        private long operationDelayMs = DEFAULT_OPERATION_DELAY_MS;
        private List<String> regions = DEFAULT_REGIONS;

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
         * Sets the regions Floci GCP offers for Compute Engine. Each region has the zones {@code <region>-a},
         * {@code -b} and {@code -c}; requests for other regions or zones are rejected as not found.
         *
         * @param regions the regions (default {@code us-central1, europe-west1})
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

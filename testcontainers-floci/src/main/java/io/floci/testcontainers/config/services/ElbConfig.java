package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Classic Elastic Load Balancing (ELB, API version 2012-06-01)-specific container settings.
 *
 * <p>Classic ELB is a separate API from ELBv2 (see {@link ElbV2Config}): a classic load balancer carries its
 * listeners inline instead of through listener and target-group resources.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ElbConfig config = ElbConfig.builder()
 *     .mock(true)
 *     .build();
 * }</pre>
 */
public class ElbConfig extends AbstractServiceConfig<ElbConfig.Builder> {

    private static final boolean DEFAULT_MOCK = false;

    private final boolean mock;

    private ElbConfig(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
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
     * Returns whether health checks are skipped: when mocked, no health check is ever probed and a
     * registered instance is {@code InService} at once.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ELB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ELB_MOCK", String.valueOf(mock));
        }
    }

    /**
     * Builder for {@link ElbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ElbConfig> {

        private boolean mock = DEFAULT_MOCK;

        private Builder() {
            // Allow instantiation only via ElbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ElbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ElbConfig instance) {
            super(instance);
            this.mock = instance.isMock();
        }

        /**
         * Sets whether health checks are skipped: when mocked, no health check is ever probed and a
         * registered instance is {@code InService} at once.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Creates an immutable {@link ElbConfig} from this builder.
         *
         * @return the Classic ELB configuration
         */
        @Override
        public ElbConfig build() {
            return new ElbConfig(this);
        }
    }
}

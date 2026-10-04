package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Security Token Service of Floci GCP.
 *
 * <p>The Security Token Service ({@code sts.googleapis.com}) exchanges external credentials (e.g. for
 * workload identity federation or downscoped tokens) for access tokens.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StsConfig config = StsConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class StsConfig extends AbstractServiceConfig<StsConfig.Builder> {

    private StsConfig(Builder builder) {
        super(builder);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_STS_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link StsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, StsConfig> {

        private Builder() {
            // Allow instantiation only via StsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link StsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(StsConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link StsConfig} from this builder.
         *
         * @return the Security Token Service configuration
         */
        @Override
        public StsConfig build() {
            return new StsConfig(this);
        }
    }
}

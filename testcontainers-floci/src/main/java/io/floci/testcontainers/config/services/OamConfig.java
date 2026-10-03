package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for OAM (CloudWatch Observability Access Manager)-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * OamConfig config = OamConfig.builder()
 *     .build();
 * }</pre>
 */
public class OamConfig extends AbstractServiceConfig<OamConfig.Builder> {

    private OamConfig(Builder builder) {
        super(builder.enabled);
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
        container.withEnv("FLOCI_SERVICES_OAM_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link OamConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, OamConfig> {

        private Builder() {
            // Allow instantiation only via OamConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link OamConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(OamConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link OamConfig} from this builder.
         *
         * @return the OAM (CloudWatch Observability Access Manager) configuration
         */
        @Override
        public OamConfig build() {
            return new OamConfig(this);
        }
    }
}

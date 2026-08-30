package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Lake Formation-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * LakeFormationConfig config = LakeFormationConfig.builder()
 *     .build();
 * }</pre>
 */
public class LakeFormationConfig extends AbstractServiceConfig<LakeFormationConfig.Builder> {

    private LakeFormationConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_LAKEFORMATION_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link LakeFormationConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, LakeFormationConfig> {

        private Builder() {
            // Allow instantiation only via LakeFormationConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link LakeFormationConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(LakeFormationConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link LakeFormationConfig} from this builder.
         *
         * @return the Lake Formation configuration
         */
        @Override
        public LakeFormationConfig build() {
            return new LakeFormationConfig(this);
        }
    }
}

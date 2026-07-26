package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for IAM-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IamConfig config = IamConfig.builder()
 *     .enforcementEnabled(true)
 *     .build();
 * }</pre>
 */
public class IamConfig extends AbstractServiceConfig<IamConfig.Builder> {

    private static final boolean DEFAULT_ENFORCEMENT_ENABLED = false;
    private static final boolean DEFAULT_SEED_DEPLOYER_PRINCIPAL = false;

    private final boolean enforcementEnabled;
    private final boolean seedDeployerPrincipal;

    private IamConfig(Builder builder) {
        super(builder.enabled);
        this.enforcementEnabled = builder.enforcementEnabled;
        this.seedDeployerPrincipal = builder.seedDeployerPrincipal;
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
     * Returns whether IAM enforcement is enabled.
     *
     * @return {@code true} if IAM enforcement is enabled
     */
    public boolean isEnforcementEnabled() {
        return enforcementEnabled;
    }

    /**
     * Returns whether the deployer principal should be seeded.
     *
     * @return {@code true} if the deployer principal should be seeded
     */
    public boolean isSeedDeployerPrincipal() {
        return seedDeployerPrincipal;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_IAM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_IAM_ENFORCEMENT_ENABLED", String.valueOf(enforcementEnabled));
            container.withEnv("FLOCI_SERVICES_IAM_SEED_DEPLOYER_PRINCIPAL", String.valueOf(seedDeployerPrincipal));
        }
    }

    /**
     * Builder for {@link IamConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IamConfig> {

        private boolean enforcementEnabled = DEFAULT_ENFORCEMENT_ENABLED;
        private boolean seedDeployerPrincipal = DEFAULT_SEED_DEPLOYER_PRINCIPAL;

        private Builder() {
            // Allow instantiation only via IamConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IamConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IamConfig instance) {
            super(instance);
            this.enforcementEnabled = instance.isEnforcementEnabled();
            this.seedDeployerPrincipal = instance.isSeedDeployerPrincipal();
        }

        /**
         * Enables or disables IAM enforcement.
         *
         * @param enforcementEnabled {@code true} to enable enforcement (default {@value DEFAULT_ENFORCEMENT_ENABLED})
         * @return this builder
         */
        public Builder enforcementEnabled(boolean enforcementEnabled) {
            this.enforcementEnabled = enforcementEnabled;
            return this;
        }

        /**
         * Enables or disables seeding the deployer principal.
         *
         * @param seedDeployerPrincipal {@code true} to seed the deployer principal (default {@value DEFAULT_SEED_DEPLOYER_PRINCIPAL})
         * @return this builder
         */
        public Builder seedDeployerPrincipal(boolean seedDeployerPrincipal) {
            this.seedDeployerPrincipal = seedDeployerPrincipal;
            return this;
        }

        /**
         * Creates an immutable {@link IamConfig} from this builder.
         *
         * @return the IAM configuration
         */
        public IamConfig build() {
            return new IamConfig(this);
        }
    }
}

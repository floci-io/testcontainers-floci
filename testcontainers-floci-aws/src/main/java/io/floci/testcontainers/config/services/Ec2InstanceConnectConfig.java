package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EC2 Instance Connect-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Ec2InstanceConnectConfig config = Ec2InstanceConnectConfig.builder()
 *     .build();
 * }</pre>
 */
public class Ec2InstanceConnectConfig extends AbstractServiceConfig<Ec2InstanceConnectConfig.Builder> {

    private Ec2InstanceConnectConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_EC2INSTANCECONNECT_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link Ec2InstanceConnectConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Ec2InstanceConnectConfig> {

        private Builder() {
            // Allow instantiation only via Ec2InstanceConnectConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Ec2InstanceConnectConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Ec2InstanceConnectConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link Ec2InstanceConnectConfig} from this builder.
         *
         * @return the EC2 Instance Connect configuration
         */
        @Override
        public Ec2InstanceConnectConfig build() {
            return new Ec2InstanceConnectConfig(this);
        }
    }
}

package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Route 53-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Route53Config config = Route53Config.builder()
 *     .defaultNameserver1("ns-1.example.com")
 *     .build();
 * }</pre>
 */
public class Route53Config extends AbstractServiceConfig<Route53Config.Builder> {

    private static final long DEFAULT_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS = 0L;

    private final String defaultNameserver1;
    private final String defaultNameserver2;
    private final String defaultNameserver3;
    private final String defaultNameserver4;
    private final long vpcAssociationControlPlaneDelayMs;

    private Route53Config(Builder builder) {
        super(builder);
        this.defaultNameserver1 = builder.defaultNameserver1;
        this.defaultNameserver2 = builder.defaultNameserver2;
        this.defaultNameserver3 = builder.defaultNameserver3;
        this.defaultNameserver4 = builder.defaultNameserver4;
        this.vpcAssociationControlPlaneDelayMs = builder.vpcAssociationControlPlaneDelayMs;
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
     * Returns the first nameserver assigned to new hosted zones, if one is configured.
     *
     * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
     *
     * @return the first default nameserver, or {@code null} if Floci assigns distinct nameservers
     */
    public String getDefaultNameserver1() {
        return defaultNameserver1;
    }

    /**
     * Returns the second nameserver assigned to new hosted zones, if one is configured.
     *
     * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
     *
     * @return the second default nameserver, or {@code null} if Floci assigns distinct nameservers
     */
    public String getDefaultNameserver2() {
        return defaultNameserver2;
    }

    /**
     * Returns the third nameserver assigned to new hosted zones, if one is configured.
     *
     * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
     *
     * @return the third default nameserver, or {@code null} if Floci assigns distinct nameservers
     */
    public String getDefaultNameserver3() {
        return defaultNameserver3;
    }

    /**
     * Returns the fourth nameserver assigned to new hosted zones, if one is configured.
     *
     * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
     *
     * @return the fourth default nameserver, or {@code null} if Floci assigns distinct nameservers
     */
    public String getDefaultNameserver4() {
        return defaultNameserver4;
    }

    /**
     * Returns the optional control-plane processing window, in milliseconds, of VPC association mutations.
     *
     * <p>A positive value makes the documented Route 53 retryable overlap errors reproducible; {@code 0}
     * keeps the immediate-completion behaviour.
     *
     * @return the optional control-plane processing window, in milliseconds, of VPC association mutations
     */
    public long getVpcAssociationControlPlaneDelayMs() {
        return vpcAssociationControlPlaneDelayMs;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ROUTE53_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (defaultNameserver1 != null) {
                container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", defaultNameserver1);
            }
            if (defaultNameserver2 != null) {
                container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2", defaultNameserver2);
            }
            if (defaultNameserver3 != null) {
                container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3", defaultNameserver3);
            }
            if (defaultNameserver4 != null) {
                container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4", defaultNameserver4);
            }
            container.withEnv("FLOCI_SERVICES_ROUTE53_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS", String.valueOf(vpcAssociationControlPlaneDelayMs));
        }
    }

    /**
     * Builder for {@link Route53Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Route53Config> {

        private String defaultNameserver1;
        private String defaultNameserver2;
        private String defaultNameserver3;
        private String defaultNameserver4;
        private long vpcAssociationControlPlaneDelayMs = DEFAULT_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS;

        private Builder() {
            // Allow instantiation only via Route53Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Route53Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Route53Config instance) {
            super(instance);
            this.defaultNameserver1 = instance.getDefaultNameserver1();
            this.defaultNameserver2 = instance.getDefaultNameserver2();
            this.defaultNameserver3 = instance.getDefaultNameserver3();
            this.defaultNameserver4 = instance.getDefaultNameserver4();
            this.vpcAssociationControlPlaneDelayMs = instance.getVpcAssociationControlPlaneDelayMs();
        }

        /**
         * Sets the first default nameserver assigned to new hosted zones.
         *
         * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
         *
         * @param defaultNameserver1 the first nameserver, or {@code null} to let Floci assign distinct nameservers
         * @return this builder
         */
        public Builder defaultNameserver1(String defaultNameserver1) {
            this.defaultNameserver1 = defaultNameserver1;
            return this;
        }

        /**
         * Sets the second default nameserver assigned to new hosted zones.
         *
         * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
         *
         * @param defaultNameserver2 the second nameserver, or {@code null} to let Floci assign distinct nameservers
         * @return this builder
         */
        public Builder defaultNameserver2(String defaultNameserver2) {
            this.defaultNameserver2 = defaultNameserver2;
            return this;
        }

        /**
         * Sets the third default nameserver assigned to new hosted zones.
         *
         * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
         *
         * @param defaultNameserver3 the third nameserver, or {@code null} to let Floci assign distinct nameservers
         * @return this builder
         */
        public Builder defaultNameserver3(String defaultNameserver3) {
            this.defaultNameserver3 = defaultNameserver3;
            return this;
        }

        /**
         * Sets the fourth default nameserver assigned to new hosted zones.
         *
         * <p>While unset, Floci assigns each delegation set its own distinct set of nameservers.
         *
         * @param defaultNameserver4 the fourth nameserver, or {@code null} to let Floci assign distinct nameservers
         * @return this builder
         */
        public Builder defaultNameserver4(String defaultNameserver4) {
            this.defaultNameserver4 = defaultNameserver4;
            return this;
        }

        /**
         * Sets the optional control-plane processing window, in milliseconds, of VPC association mutations.
         *
         * <p>A positive value makes the documented Route 53 retryable overlap errors reproducible; {@code 0}
         * keeps the immediate-completion behaviour.
         *
         * @param vpcAssociationControlPlaneDelayMs the optional control-plane processing window, in milliseconds, of VPC association mutations (default {@value DEFAULT_VPC_ASSOCIATION_CONTROL_PLANE_DELAY_MS})
         * @return this builder
         */
        public Builder vpcAssociationControlPlaneDelayMs(long vpcAssociationControlPlaneDelayMs) {
            this.vpcAssociationControlPlaneDelayMs = vpcAssociationControlPlaneDelayMs;
            return this;
        }

        /**
         * Creates an immutable {@link Route53Config} from this builder.
         *
         * @return the Route 53 configuration
         */
        @Override
        public Route53Config build() {
            return new Route53Config(this);
        }
    }
}

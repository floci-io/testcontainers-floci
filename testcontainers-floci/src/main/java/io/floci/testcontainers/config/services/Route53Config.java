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

    private static final String DEFAULT_NAMESERVER_1 = "ns-1.awsdns-01.org";
    private static final String DEFAULT_NAMESERVER_2 = "ns-2.awsdns-02.net";
    private static final String DEFAULT_NAMESERVER_3 = "ns-3.awsdns-03.com";
    private static final String DEFAULT_NAMESERVER_4 = "ns-4.awsdns-04.co.uk";

    private final String defaultNameserver1;
    private final String defaultNameserver2;
    private final String defaultNameserver3;
    private final String defaultNameserver4;

    private Route53Config(Builder builder) {
        super(builder.enabled);
        this.defaultNameserver1 = builder.defaultNameserver1;
        this.defaultNameserver2 = builder.defaultNameserver2;
        this.defaultNameserver3 = builder.defaultNameserver3;
        this.defaultNameserver4 = builder.defaultNameserver4;
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
     * Returns the first default nameserver.
     *
     * @return the first default nameserver
     */
    public String getDefaultNameserver1() {
        return defaultNameserver1;
    }

    /**
     * Returns the second default nameserver.
     *
     * @return the second default nameserver
     */
    public String getDefaultNameserver2() {
        return defaultNameserver2;
    }

    /**
     * Returns the third default nameserver.
     *
     * @return the third default nameserver
     */
    public String getDefaultNameserver3() {
        return defaultNameserver3;
    }

    /**
     * Returns the fourth default nameserver.
     *
     * @return the fourth default nameserver
     */
    public String getDefaultNameserver4() {
        return defaultNameserver4;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ROUTE53_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_1", defaultNameserver1);
            container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_2", defaultNameserver2);
            container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_3", defaultNameserver3);
            container.withEnv("FLOCI_SERVICES_ROUTE53_DEFAULT_NAMESERVER_4", defaultNameserver4);
        }
    }

    /**
     * Builder for {@link Route53Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Route53Config> {

        private String defaultNameserver1 = DEFAULT_NAMESERVER_1;
        private String defaultNameserver2 = DEFAULT_NAMESERVER_2;
        private String defaultNameserver3 = DEFAULT_NAMESERVER_3;
        private String defaultNameserver4 = DEFAULT_NAMESERVER_4;

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
        }

        /**
         * Sets the first default nameserver assigned to new hosted zones.
         *
         * @param defaultNameserver1 the first nameserver (default {@value DEFAULT_NAMESERVER_1})
         * @return this builder
         */
        public Builder defaultNameserver1(String defaultNameserver1) {
            this.defaultNameserver1 = defaultNameserver1;
            return this;
        }

        /**
         * Sets the second default nameserver assigned to new hosted zones.
         *
         * @param defaultNameserver2 the second nameserver (default {@value DEFAULT_NAMESERVER_2})
         * @return this builder
         */
        public Builder defaultNameserver2(String defaultNameserver2) {
            this.defaultNameserver2 = defaultNameserver2;
            return this;
        }

        /**
         * Sets the third default nameserver assigned to new hosted zones.
         *
         * @param defaultNameserver3 the third nameserver (default {@value DEFAULT_NAMESERVER_3})
         * @return this builder
         */
        public Builder defaultNameserver3(String defaultNameserver3) {
            this.defaultNameserver3 = defaultNameserver3;
            return this;
        }

        /**
         * Sets the fourth default nameserver assigned to new hosted zones.
         *
         * @param defaultNameserver4 the fourth nameserver (default {@value DEFAULT_NAMESERVER_4})
         * @return this builder
         */
        public Builder defaultNameserver4(String defaultNameserver4) {
            this.defaultNameserver4 = defaultNameserver4;
            return this;
        }

        /**
         * Creates an immutable {@link Route53Config} from this builder.
         *
         * @return the Route 53 configuration
         */
        public Route53Config build() {
            return new Route53Config(this);
        }
    }
}

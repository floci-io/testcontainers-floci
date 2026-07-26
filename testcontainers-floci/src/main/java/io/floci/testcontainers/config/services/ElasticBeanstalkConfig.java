package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Elastic Beanstalk-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ElasticBeanstalkConfig config = ElasticBeanstalkConfig.builder()
 *     .build();
 * }</pre>
 */
public class ElasticBeanstalkConfig extends AbstractServiceConfig<ElasticBeanstalkConfig.Builder> {

    private ElasticBeanstalkConfig(Builder builder) {
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
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ELASTICBEANSTALK_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ElasticBeanstalkConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ElasticBeanstalkConfig> {


        private Builder() {
            // Allow instantiation only via ElasticBeanstalkConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ElasticBeanstalkConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ElasticBeanstalkConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ElasticBeanstalkConfig} from this builder.
         *
         * @return the Elastic Beanstalk configuration
         */
        public ElasticBeanstalkConfig build() {
            return new ElasticBeanstalkConfig(this);
        }
    }
}

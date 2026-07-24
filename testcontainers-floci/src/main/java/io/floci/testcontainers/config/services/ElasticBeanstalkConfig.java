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
public class ElasticBeanstalkConfig extends AbstractServiceConfig {

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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ELASTICBEANSTALK_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ElasticBeanstalkConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;

        private Builder() {
            // Allow instantiation only via ElasticBeanstalkConfig.builder()
        }

        /**
         * Enables or disables the Elastic Beanstalk service.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
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

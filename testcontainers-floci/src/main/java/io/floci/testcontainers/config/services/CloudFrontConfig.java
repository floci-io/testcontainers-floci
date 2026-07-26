package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudFront-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudFrontConfig config = CloudFrontConfig.builder()
 *     .domainSuffix("example.com")
 *     .build();
 * }</pre>
 */
public class CloudFrontConfig extends AbstractServiceConfig<CloudFrontConfig.Builder> {

    private static final String DEFAULT_DOMAIN_SUFFIX = "cloudfront.net";

    private final String domainSuffix;

    private CloudFrontConfig(Builder builder) {
        super(builder.enabled);
        this.domainSuffix = builder.domainSuffix;
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
     * Returns the domain suffix used for CloudFront distributions.
     *
     * @return the domain suffix (default {@value DEFAULT_DOMAIN_SUFFIX})
     */
    public String getDomainSuffix() {
        return domainSuffix;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDFRONT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDFRONT_DOMAIN_SUFFIX", domainSuffix);
        }
    }

    /**
     * Builder for {@link CloudFrontConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudFrontConfig> {

        private String domainSuffix = DEFAULT_DOMAIN_SUFFIX;

        private Builder() {
            // Allow instantiation only via CloudFrontConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudFrontConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudFrontConfig instance) {
            super(instance);
            this.domainSuffix = instance.getDomainSuffix();
        }

        /**
         * Sets the domain suffix used for CloudFront distribution domain names.
         *
         * @param domainSuffix the domain suffix (default {@value DEFAULT_DOMAIN_SUFFIX})
         * @return this builder
         */
        public Builder domainSuffix(String domainSuffix) {
            this.domainSuffix = domainSuffix;
            return this;
        }

        /**
         * Creates an immutable {@link CloudFrontConfig} from this builder.
         *
         * @return the CloudFront configuration
         */
        public CloudFrontConfig build() {
            return new CloudFrontConfig(this);
        }
    }
}

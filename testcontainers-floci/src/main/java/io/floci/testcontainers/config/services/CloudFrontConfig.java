package io.floci.testcontainers.config.services;

import java.util.List;
import java.util.Optional;
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
    private final List<String> allowedPrivateOriginHosts;

    private CloudFrontConfig(Builder builder) {
        super(builder.enabled);
        this.domainSuffix = builder.domainSuffix;
        this.allowedPrivateOriginHosts = builder.allowedPrivateOriginHosts;
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

    /**
     * Returns the exact custom-origin hostnames allowed to resolve to private or otherwise
     * non-routable addresses, or {@link Optional#empty()} if none are configured.
     *
     * <p>Empty by default to match CloudFront's public custom-origin boundary.
     *
     * @return the allowed private custom-origin hostnames, or {@link Optional#empty()} if none are configured
     */
    public Optional<List<String>> getAllowedPrivateOriginHosts() {
        return Optional.ofNullable(allowedPrivateOriginHosts);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDFRONT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDFRONT_DOMAIN_SUFFIX", domainSuffix);

            if (allowedPrivateOriginHosts != null && !allowedPrivateOriginHosts.isEmpty()) {
                container.withEnv("FLOCI_SERVICES_CLOUDFRONT_ALLOWED_PRIVATE_ORIGIN_HOSTS",
                        String.join(",", allowedPrivateOriginHosts));
            }
        }
    }

    /**
     * Builder for {@link CloudFrontConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudFrontConfig> {

        private String domainSuffix = DEFAULT_DOMAIN_SUFFIX;
        private List<String> allowedPrivateOriginHosts = null;

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
            this.allowedPrivateOriginHosts = instance.getAllowedPrivateOriginHosts().orElse(null);
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
         * Sets the exact custom-origin hostnames allowed to resolve to private or otherwise
         * non-routable addresses.
         *
         * @param allowedPrivateOriginHosts the allowed private custom-origin hostnames, or
         *                                  {@code null} to unset (default: empty, matching
         *                                  CloudFront's public custom-origin boundary)
         * @return this builder
         */
        public Builder allowedPrivateOriginHosts(List<String> allowedPrivateOriginHosts) {
            this.allowedPrivateOriginHosts = allowedPrivateOriginHosts == null ? null : List.copyOf(allowedPrivateOriginHosts);
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

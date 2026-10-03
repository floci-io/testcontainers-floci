package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for Verified Permissions-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * VerifiedPermissionsConfig config = VerifiedPermissionsConfig.builder()
 *     .build();
 * }</pre>
 */
public class VerifiedPermissionsConfig extends AbstractServiceConfig<VerifiedPermissionsConfig.Builder> {

    private static final String DEFAULT_CEDAR_IMAGE = "floci/floci-sidecar-cedar:1.1.0";

    private final String cedarUrl;
    private final String cedarImage;

    private VerifiedPermissionsConfig(Builder builder) {
        super(builder.enabled);
        this.cedarUrl = builder.cedarUrl;
        this.cedarImage = builder.cedarImage;
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
     * Returns the URL of an already running Cedar sidecar.
     *
     * <p>When set, Floci evaluates authorization requests against this URL and skips Cedar sidecar container
     * management.
     *
     * @return the URL of an already running Cedar sidecar, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getCedarUrl() {
        return Optional.ofNullable(cedarUrl);
    }

    /**
     * Returns the image of the Cedar sidecar container Floci runs to evaluate authorization requests.
     *
     * @return the image of the Cedar sidecar container Floci runs to evaluate authorization requests
     */
    public String getCedarImage() {
        return cedarImage;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_VERIFIEDPERMISSIONS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (cedarUrl != null) {
                container.withEnv("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_URL", cedarUrl);
            }

            container.withEnv("FLOCI_SERVICES_VERIFIEDPERMISSIONS_CEDAR_IMAGE", cedarImage);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        // Authorization requests are evaluated by a Cedar sidecar container, unless an external one is configured
        return isEnabled() && cedarUrl == null;
    }

    /**
     * Builder for {@link VerifiedPermissionsConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, VerifiedPermissionsConfig> {

        private String cedarUrl;
        private String cedarImage = DEFAULT_CEDAR_IMAGE;

        private Builder() {
            // Allow instantiation only via VerifiedPermissionsConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link VerifiedPermissionsConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(VerifiedPermissionsConfig instance) {
            super(instance);
            this.cedarUrl = instance.getCedarUrl().orElse(null);
            this.cedarImage = instance.getCedarImage();
        }

        /**
         * Sets the URL of an already running Cedar sidecar.
         *
         * <p>When set, Floci evaluates authorization requests against this URL and skips Cedar sidecar
         * container management.
         *
         * @param cedarUrl the URL of an already running Cedar sidecar, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder cedarUrl(String cedarUrl) {
            this.cedarUrl = cedarUrl;
            return this;
        }

        /**
         * Sets the image of the Cedar sidecar container Floci runs to evaluate authorization requests.
         *
         * @param cedarImage the image of the Cedar sidecar container Floci runs to evaluate authorization requests (default {@value DEFAULT_CEDAR_IMAGE})
         * @return this builder
         */
        public Builder cedarImage(String cedarImage) {
            this.cedarImage = cedarImage;
            return this;
        }

        /**
         * Creates an immutable {@link VerifiedPermissionsConfig} from this builder.
         *
         * @return the Verified Permissions configuration
         */
        @Override
        public VerifiedPermissionsConfig build() {
            return new VerifiedPermissionsConfig(this);
        }
    }
}

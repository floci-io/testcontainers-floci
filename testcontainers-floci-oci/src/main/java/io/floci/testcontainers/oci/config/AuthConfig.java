package io.floci.testcontainers.oci.config;

import org.testcontainers.containers.Container;

/**
 * Authentication settings of the Floci OCI server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AuthConfig config = AuthConfig.builder()
 *     .requireSignature(true)
 *     .build();
 * }</pre>
 */
public class AuthConfig {

    private static final boolean DEFAULT_REQUIRE_SIGNATURE = false;

    private final boolean requireSignature;

    private AuthConfig(Builder builder) {
        this.requireSignature = builder.requireSignature;
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
     * Returns whether requests without a structurally valid OCI Signature {@code Authorization} header are
     * rejected with {@code 401 NotAuthenticated}. The RSA signature itself is never verified.
     *
     * @return {@code true} if a signature is required
     */
    public boolean isRequireSignature() {
        return requireSignature;
    }

    /**
     * Applies this authentication configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_OCI_AUTH_REQUIRE_SIGNATURE", String.valueOf(requireSignature));
    }

    /**
     * Builder for {@link AuthConfig}.
     */
    public static class Builder {

        private boolean requireSignature = DEFAULT_REQUIRE_SIGNATURE;

        private Builder() {
            // Allow instantiation only via AuthConfig.builder()
        }

        private Builder(AuthConfig instance) {
            this.requireSignature = instance.requireSignature;
        }

        /**
         * Sets whether requests without a structurally valid OCI Signature {@code Authorization} header are
         * rejected with {@code 401 NotAuthenticated}. Floci OCI only parses the signature for tenancy and user
         * context; the RSA signature itself is never verified, so any locally generated key works. Object Storage
         * pre-authenticated requests are exempt. Note that Floci OCI currently also rejects unsigned requests to
         * its own {@code /_floci-oci/...} endpoints (e.g. health) while this is enabled.
         *
         * @param requireSignature {@code true} to require signed requests (default {@value DEFAULT_REQUIRE_SIGNATURE})
         * @return this builder
         */
        public Builder requireSignature(boolean requireSignature) {
            this.requireSignature = requireSignature;
            return this;
        }

        /**
         * Creates an immutable {@link AuthConfig} from this builder.
         *
         * @return the authentication configuration
         */
        public AuthConfig build() {
            return new AuthConfig(this);
        }
    }
}

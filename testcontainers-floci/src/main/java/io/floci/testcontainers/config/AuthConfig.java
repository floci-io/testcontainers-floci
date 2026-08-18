package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

/**
 * Authentication-related configuration for the Floci server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AuthConfig config = AuthConfig.builder()
 *     .validateSignatures(true)
 *     .build();
 * }</pre>
 */
public class AuthConfig {

    private static final boolean DEFAULT_VALIDATE_SIGNATURES = false;
    private static final String DEFAULT_PRESIGN_SECRET = "local-emulator-secret";

    private final boolean validateSignatures;
    private final String presignSecret;

    private AuthConfig(Builder builder) {
        this.validateSignatures = builder.validateSignatures;
        this.presignSecret = builder.presignSecret;
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
     * Returns whether SigV4 request signatures are validated.
     *
     * @return {@code true} if request signatures are validated
     */
    public boolean isValidateSignatures() {
        return validateSignatures;
    }

    /**
     * Returns the secret used to validate presigned URLs.
     *
     * @return the presign secret
     */
    public String getPresignSecret() {
        return presignSecret;
    }

    /**
     * Applies this auth configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AUTH_VALIDATE_SIGNATURES", String.valueOf(validateSignatures));
        container.withEnv("FLOCI_AUTH_PRESIGN_SECRET", presignSecret);
    }

    /**
     * Builder for {@link AuthConfig}.
     */
    public static class Builder {

        private boolean validateSignatures = DEFAULT_VALIDATE_SIGNATURES;
        private String presignSecret = DEFAULT_PRESIGN_SECRET;

        private Builder() {
            // Allow instantiation only via AuthConfig.builder()
        }

        private Builder(AuthConfig instance) {
            this.validateSignatures = instance.validateSignatures;
            this.presignSecret = instance.presignSecret;
        }

        /**
         * Sets whether SigV4 request signatures are validated.
         *
         * @param validateSignatures {@code true} to validate request signatures
         *                           (default {@value DEFAULT_VALIDATE_SIGNATURES})
         * @return this builder
         */
        public Builder validateSignatures(boolean validateSignatures) {
            this.validateSignatures = validateSignatures;
            return this;
        }

        /**
         * Sets the secret used to validate presigned URLs.
         *
         * @param presignSecret the presign secret (default {@value DEFAULT_PRESIGN_SECRET})
         * @return this builder
         */
        public Builder presignSecret(String presignSecret) {
            this.presignSecret = presignSecret;
            return this;
        }

        /**
         * Creates an immutable {@link AuthConfig} from this builder.
         *
         * @return the auth configuration
         */
        public AuthConfig build() {
            return new AuthConfig(this);
        }
    }
}

package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

/**
 * Protocol-related configuration for the Floci server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ProtocolsConfig config = ProtocolsConfig.builder()
 *     .strictClaiming(true)
 *     .build();
 * }</pre>
 */
public class ProtocolsConfig {

    private static final boolean DEFAULT_STRICT_CLAIMING = false;
    private static final boolean DEFAULT_REJECT_UNKNOWN_SERVICE_SCOPE = true;
    private static final int DEFAULT_MAX_REQUEST_SIZE = 2048;

    private final boolean strictClaiming;
    private final boolean rejectUnknownServiceScope;
    private final int maxRequestSize;

    private ProtocolsConfig(Builder builder) {
        this.strictClaiming = builder.strictClaiming;
        this.rejectUnknownServiceScope = builder.rejectUnknownServiceScope;
        this.maxRequestSize = builder.maxRequestSize;
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
     * Returns whether strict RPC protocol claiming is enabled.
     *
     * <p>When enabled, requests carrying an RPC protocol signal that no supported wire
     * protocol claims are rejected per the Smithy wire-protocol-selection guide (e.g. an
     * unknown Smithy-Protocol header value, a recognized-but-unimplemented rpc-v2-json
     * request, or an X-Amz-Target post with a foreign content type). When disabled such
     * requests are only logged and pass through to JAX-RS matching.
     *
     * @return {@code true} if strict RPC protocol claiming is enabled
     */
    public boolean isStrictClaiming() {
        return strictClaiming;
    }

    /**
     * Returns whether requests carrying an unrecognized SigV4 credential scope are rejected.
     *
     * <p>When enabled, a REST request whose SigV4 credential scope names a service absent
     * from the catalog is rejected with {@code UnknownOperationException} instead of falling
     * through JAX-RS matching into S3's path-style routes, where it surfaces as a misleading
     * {@code NoSuchBucket} (issue #1754).
     *
     * <p>On by default. Turn it off if Floci serves a route whose signing scope is not yet
     * enumerated in the catalog: the request then falls through as it did before, rather than
     * failing with a 404 that has no workaround.
     *
     * @return {@code true} if requests with an unknown SigV4 service scope are rejected
     */
    public boolean isRejectUnknownServiceScope() {
        return rejectUnknownServiceScope;
    }

    /**
     * Returns the maximum accepted request size, in bytes.
     *
     * @return the maximum request size in bytes
     */
    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * Applies this protocols configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_PROTOCOLS_STRICT_CLAIMING", String.valueOf(strictClaiming));
        container.withEnv("FLOCI_PROTOCOLS_REJECT_UNKNOWN_SERVICE_SCOPE", String.valueOf(rejectUnknownServiceScope));
        container.withEnv("FLOCI_MAX_REQUEST_SIZE", String.valueOf(maxRequestSize)); // Env var is correct as the config is structured differently in Floci
    }

    /**
     * Builder for {@link ProtocolsConfig}.
     */
    public static class Builder {

        private boolean strictClaiming = DEFAULT_STRICT_CLAIMING;
        private boolean rejectUnknownServiceScope = DEFAULT_REJECT_UNKNOWN_SERVICE_SCOPE;
        private int maxRequestSize = DEFAULT_MAX_REQUEST_SIZE;

        private Builder() {
            // Allow instantiation only via ProtocolsConfig.builder()
        }

        private Builder(ProtocolsConfig instance) {
            this.strictClaiming = instance.strictClaiming;
            this.rejectUnknownServiceScope = instance.rejectUnknownServiceScope;
            this.maxRequestSize = instance.maxRequestSize;
        }

        /**
         * Sets whether strict RPC protocol claiming is enabled.
         *
         * @param strictClaiming {@code true} to reject requests carrying an RPC protocol
         *                       signal that no supported wire protocol claims (default {@value DEFAULT_STRICT_CLAIMING})
         * @return this builder
         */
        public Builder strictClaiming(boolean strictClaiming) {
            this.strictClaiming = strictClaiming;
            return this;
        }

        /**
         * Sets whether requests carrying an unrecognized SigV4 credential scope are rejected.
         *
         * @param rejectUnknownServiceScope {@code true} to reject a REST request whose SigV4
         *                                  credential scope names a service absent from the
         *                                  catalog with {@code UnknownOperationException}
         *                                  instead of letting it fall through to S3's
         *                                  path-style routes (default {@value DEFAULT_REJECT_UNKNOWN_SERVICE_SCOPE})
         * @return this builder
         */
        public Builder rejectUnknownServiceScope(boolean rejectUnknownServiceScope) {
            this.rejectUnknownServiceScope = rejectUnknownServiceScope;
            return this;
        }

        /**
         * Sets the maximum accepted request size, in bytes.
         *
         * @param maxRequestSize the maximum request size in bytes (default {@value DEFAULT_MAX_REQUEST_SIZE})
         * @return this builder
         */
        public Builder maxRequestSize(int maxRequestSize) {
            this.maxRequestSize = maxRequestSize;
            return this;
        }

        /**
         * Creates an immutable {@link ProtocolsConfig} from this builder.
         *
         * @return the protocols configuration
         */
        public ProtocolsConfig build() {
            return new ProtocolsConfig(this);
        }
    }
}

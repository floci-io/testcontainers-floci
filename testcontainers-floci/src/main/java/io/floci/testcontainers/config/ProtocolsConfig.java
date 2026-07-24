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

    private final boolean strictClaiming;

    private ProtocolsConfig(Builder builder) {
        this.strictClaiming = builder.strictClaiming;
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
     * Applies this protocols configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_PROTOCOLS_STRICT_CLAIMING", String.valueOf(strictClaiming));
    }

    /**
     * Builder for {@link ProtocolsConfig}.
     */
    public static class Builder {

        private boolean strictClaiming = DEFAULT_STRICT_CLAIMING;

        private Builder() {
            // Allow instantiation only via ProtocolsConfig.builder()
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
         * Creates an immutable {@link ProtocolsConfig} from this builder.
         *
         * @return the protocols configuration
         */
        public ProtocolsConfig build() {
            return new ProtocolsConfig(this);
        }
    }
}

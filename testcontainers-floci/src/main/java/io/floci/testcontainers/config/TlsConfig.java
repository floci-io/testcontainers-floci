package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Optional TLS configuration for enabling HTTPS on the Floci server.
 * When enabled, all endpoints are reachable via {@code https://} and
 * WebSocket connections work via {@code wss://}.
 *
 * <p>Both HTTP and HTTPS are served simultaneously (LocalStack parity).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * TlsConfig config = TlsConfig.builder()
 *     .enabled(true)
 *     .selfSigned(true)
 *     .build();
 * }</pre>
 */
public class TlsConfig {

    private static final boolean DEFAULT_ENABLED = false;
    private static final boolean DEFAULT_SELF_SIGNED = true;
    private static final int DEFAULT_AWS_HTTPS_PORT = 443;

    private final boolean enabled;
    private final String certPath;
    private final String keyPath;
    private final boolean selfSigned;
    private final int awsHttpsPort;

    private TlsConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.certPath = builder.certPath;
        this.keyPath = builder.keyPath;
        this.selfSigned = builder.selfSigned;
        this.awsHttpsPort = builder.awsHttpsPort;
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
     * Returns whether TLS/HTTPS is enabled on the server.
     *
     * @return {@code true} if TLS is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the path to the PEM certificate file, or empty if not set.
     *
     * @return the certificate path, or empty
     */
    public Optional<String> getCertPath() {
        return Optional.ofNullable(certPath);
    }

    /**
     * Returns the path to the PEM private key file, or empty if not set.
     *
     * @return the private key path, or empty
     */
    public Optional<String> getKeyPath() {
        return Optional.ofNullable(keyPath);
    }

    /**
     * Returns whether a self-signed certificate should be auto-generated when no
     * {@code certPath}/{@code keyPath} is provided.
     *
     * @return {@code true} if self-signed certificate generation is enabled
     */
    public boolean isSelfSigned() {
        return selfSigned;
    }

    /**
     * Returns the additional port the TLS proxy binds for AWS-style HTTPS traffic, alongside the
     * public Floci port.
     *
     * <p>CDK/CloudFormation custom resources send their {@code cfn-response} callback with
     * bundled code that hardcodes {@code https://} and ignores the port in the ResponseURL,
     * so the PUT lands on the conventional 443 regardless of Floci's configured port. Binding
     * 443 here (with the same HTTP/HTTPS protocol detection used on the main port) lets those
     * callbacks — and any other client that assumes AWS lives on 443 — reach Floci.
     *
     * <p>Default {@value DEFAULT_AWS_HTTPS_PORT}. Set to {@code 0} to disable the extra binding
     * (e.g. when Floci runs unprivileged or another process owns 443). When equal to the main
     * Floci port only a single listener is started.
     *
     * @return the additional AWS-style HTTPS port
     */
    public int getAwsHttpsPort() {
        return awsHttpsPort;
    }

    /**
     * Applies this TLS configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_TLS_ENABLED", String.valueOf(enabled));

        if (enabled) {
            container.withEnv("FLOCI_TLS_SELF_SIGNED", String.valueOf(selfSigned));

            if (certPath != null) {
                container.withEnv("FLOCI_TLS_CERT_PATH", certPath);
            }
            if (keyPath != null) {
                container.withEnv("FLOCI_TLS_KEY_PATH", keyPath);
            }

            container.withEnv("FLOCI_TLS_AWS_HTTPS_PORT", String.valueOf(awsHttpsPort));
        }
    }

    /**
     * Builder for {@link TlsConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private String certPath;
        private String keyPath;
        private boolean selfSigned = DEFAULT_SELF_SIGNED;
        private int awsHttpsPort = DEFAULT_AWS_HTTPS_PORT;

        private Builder() {
            // Allow instantiation only via TlsConfig.builder()
        }

        private Builder(TlsConfig instance) {
            this.enabled = instance.enabled;
            this.certPath = instance.certPath;
            this.keyPath = instance.keyPath;
            this.selfSigned = instance.selfSigned;
            this.awsHttpsPort = instance.awsHttpsPort;
        }

        /**
         * Enables or disables TLS/HTTPS on the server.
         *
         * @param enabled {@code true} to enable (default {@value DEFAULT_ENABLED})
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Sets the path to the PEM certificate file. When set together with
         * {@link #keyPath(String)}, the provided certificate is used instead of a
         * generated one.
         *
         * @param certPath the path to the PEM certificate file, or {@code null} to unset
         * @return this builder
         */
        public Builder certPath(String certPath) {
            this.certPath = certPath;
            return this;
        }

        /**
         * Sets the path to the PEM private key file. When set together with
         * {@link #certPath(String)}, the provided key is used instead of a generated one.
         *
         * @param keyPath the path to the PEM private key file, or {@code null} to unset
         * @return this builder
         */
        public Builder keyPath(String keyPath) {
            this.keyPath = keyPath;
            return this;
        }

        /**
         * Sets whether a self-signed certificate should be auto-generated when no
         * {@code certPath}/{@code keyPath} is provided. The generated files are persisted
         * to {@code {storage.persistent-path}/tls/} and reused across restarts.
         *
         * @param selfSigned {@code true} to auto-generate a self-signed certificate (default {@value DEFAULT_SELF_SIGNED})
         * @return this builder
         */
        public Builder selfSigned(boolean selfSigned) {
            this.selfSigned = selfSigned;
            return this;
        }

        /**
         * Sets the additional port the TLS proxy binds for AWS-style HTTPS traffic, alongside the
         * public Floci port.
         *
         * <p>CDK/CloudFormation custom resources send their {@code cfn-response} callback with
         * bundled code that hardcodes {@code https://} and ignores the port in the ResponseURL,
         * so the PUT lands on the conventional 443 regardless of Floci's configured port. Binding
         * 443 here (with the same HTTP/HTTPS protocol detection used on the main port) lets those
         * callbacks — and any other client that assumes AWS lives on 443 — reach Floci.
         *
         * <p>Set to {@code 0} to disable the extra binding (e.g. when Floci runs unprivileged or
         * another process owns 443). When equal to the main Floci port only a single listener is
         * started.
         *
         * @param awsHttpsPort the additional AWS-style HTTPS port (default {@value DEFAULT_AWS_HTTPS_PORT})
         * @return this builder
         */
        public Builder awsHttpsPort(int awsHttpsPort) {
            this.awsHttpsPort = awsHttpsPort;
            return this;
        }

        /**
         * Creates an immutable {@link TlsConfig} from this builder.
         *
         * @return the TLS configuration
         */
        public TlsConfig build() {
            return new TlsConfig(this);
        }
    }
}

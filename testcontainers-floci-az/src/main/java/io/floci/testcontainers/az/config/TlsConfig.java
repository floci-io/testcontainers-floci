package io.floci.testcontainers.az.config;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Optional TLS configuration for enabling HTTPS on the Floci Azure server.
 * When enabled, all endpoints are reachable via {@code https://} on the same port as plain HTTP
 * (protocol sniffing), so both are served simultaneously.
 *
 * <p>Several Azure SDKs (e.g. Cosmos DB and Key Vault) and the Terraform/OpenTofu {@code azurerm}
 * provider require HTTPS. The certificate clients have to trust is served at
 * {@code GET /_floci/tls-cert} (see {@code FlociAzContainer#getTlsCertificate()}).
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
    private static final boolean DEFAULT_PUBLIC_HOSTS = false;
    private static final int DEFAULT_HTTPS_PORT = 0;

    private final boolean enabled;
    private final String certPath;
    private final String keyPath;
    private final String caCertPath;
    private final String caKeyPath;
    private final boolean selfSigned;
    private final boolean publicHosts;
    private final int httpsPort;

    private TlsConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.certPath = builder.certPath;
        this.keyPath = builder.keyPath;
        this.caCertPath = builder.caCertPath;
        this.caKeyPath = builder.caKeyPath;
        this.selfSigned = builder.selfSigned;
        this.publicHosts = builder.publicHosts;
        this.httpsPort = builder.httpsPort;
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
     * Returns the path, inside the container, to the PEM certificate of a CA of your own (e.g. mkcert's
     * {@code rootCA.pem}), or empty if not set. Together with {@link #getCaKeyPath()}, Floci Azure issues its
     * server certificates from that CA instead of generating one. Ignored when {@code certPath} and
     * {@code keyPath} are set.
     *
     * @return the CA certificate path, or empty
     */
    public Optional<String> getCaCertPath() {
        return Optional.ofNullable(caCertPath);
    }

    /**
     * Returns the path, inside the container, to the PEM private key (PKCS#1, PKCS#8 or SEC1) of the CA
     * named by {@link #getCaCertPath()}, or empty if not set.
     *
     * @return the CA private key path, or empty
     */
    public Optional<String> getCaKeyPath() {
        return Optional.ofNullable(caKeyPath);
    }

    /**
     * Returns whether a self-signed certificate should be auto-generated when no
     * {@code certPath}/{@code keyPath} or CA paths are provided.
     *
     * @return {@code true} if self-signed certificate generation is enabled
     */
    public boolean isSelfSigned() {
        return selfSigned;
    }

    /**
     * Returns whether Floci Azure also answers for Azure's own public-cloud hosts
     * ({@code management.azure.com} and the others), in the self-signed certificate and the embedded DNS,
     * for clients that hardcode them (such as azd).
     *
     * @return {@code true} if Azure's public hosts are served
     */
    public boolean isPublicHosts() {
        return publicHosts;
    }

    /**
     * Returns the additional port the TLS proxy binds for HTTPS traffic, alongside the public
     * Floci Azure port.
     *
     * <p>Azure SDKs default to {@code https://} endpoints on the conventional 443 unless an explicit
     * port is configured. Binding 443 inside the container (with the same HTTP/HTTPS protocol detection
     * used on the main port) lets clients that assume Azure lives on 443 reach Floci Azure. The port is
     * not exposed by this container; clients outside the container use the mapped main port.
     *
     * <p>Default {@value DEFAULT_HTTPS_PORT}, which disables the extra binding: Floci Azure defaults to 443, but
     * this module turns it off (via the container's default environment), since binding a privileged port
     * inside the container is not needed for tests and the main port serves HTTPS as well.
     *
     * @return the additional HTTPS port
     */
    public int getHttpsPort() {
        return httpsPort;
    }

    /**
     * Applies this TLS configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_TLS_ENABLED", String.valueOf(enabled));

        if (enabled) {
            container.withEnv("FLOCI_AZ_TLS_SELF_SIGNED", String.valueOf(selfSigned));

            if (certPath != null) {
                container.withEnv("FLOCI_AZ_TLS_CERT_PATH", certPath);
            }
            if (keyPath != null) {
                container.withEnv("FLOCI_AZ_TLS_KEY_PATH", keyPath);
            }
            if (caCertPath != null) {
                container.withEnv("FLOCI_AZ_TLS_CA_CERT_PATH", caCertPath);
            }
            if (caKeyPath != null) {
                container.withEnv("FLOCI_AZ_TLS_CA_KEY_PATH", caKeyPath);
            }

            container.withEnv("FLOCI_AZ_TLS_PUBLIC_HOSTS", String.valueOf(publicHosts));
            container.withEnv("FLOCI_AZ_TLS_HTTPS_PORT", String.valueOf(httpsPort));
        }
    }

    /**
     * Builder for {@link TlsConfig}.
     */
    public static class Builder {

        private boolean enabled = DEFAULT_ENABLED;
        private String certPath;
        private String keyPath;
        private String caCertPath;
        private String caKeyPath;
        private boolean selfSigned = DEFAULT_SELF_SIGNED;
        private boolean publicHosts = DEFAULT_PUBLIC_HOSTS;
        private int httpsPort = DEFAULT_HTTPS_PORT;

        private Builder() {
            // Allow instantiation only via TlsConfig.builder()
        }

        private Builder(TlsConfig instance) {
            this.enabled = instance.enabled;
            this.certPath = instance.certPath;
            this.keyPath = instance.keyPath;
            this.caCertPath = instance.caCertPath;
            this.caKeyPath = instance.caKeyPath;
            this.selfSigned = instance.selfSigned;
            this.publicHosts = instance.publicHosts;
            this.httpsPort = instance.httpsPort;
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
         * Sets the path, inside the container, to the PEM certificate of a CA of your own (e.g. mkcert's
         * {@code rootCA.pem}). Together with {@link #caKeyPath(String)}, Floci Azure issues its server
         * certificates from that CA instead of generating one, so clients that already trust it need no
         * extra step. Ignored when {@link #certPath(String)} and {@link #keyPath(String)} are set.
         *
         * @param caCertPath the path to the CA's PEM certificate, or {@code null} to unset
         * @return this builder
         */
        public Builder caCertPath(String caCertPath) {
            this.caCertPath = caCertPath;
            return this;
        }

        /**
         * Sets the path, inside the container, to the PEM private key (PKCS#1, PKCS#8 or SEC1) of the CA
         * set via {@link #caCertPath(String)}. Floci Azure reads it and never copies it.
         *
         * @param caKeyPath the path to the CA's PEM private key, or {@code null} to unset
         * @return this builder
         */
        public Builder caKeyPath(String caKeyPath) {
            this.caKeyPath = caKeyPath;
            return this;
        }

        /**
         * Sets whether a self-signed certificate should be auto-generated when no
         * {@code certPath}/{@code keyPath} or CA paths are provided.
         *
         * @param selfSigned {@code true} to auto-generate a self-signed certificate (default {@value DEFAULT_SELF_SIGNED})
         * @return this builder
         */
        public Builder selfSigned(boolean selfSigned) {
            this.selfSigned = selfSigned;
            return this;
        }

        /**
         * Sets whether Floci Azure also answers for Azure's own public-cloud hosts
         * ({@code management.azure.com} and the others), in the self-signed certificate and the embedded
         * DNS, for clients that hardcode them and cannot be pointed at a custom cloud (such as azd). Off by
         * default: a CA a client trusts can then sign Azure's real names, so it should only be trusted where
         * that is intended.
         *
         * @param publicHosts {@code true} to serve Azure's public hosts (default {@value DEFAULT_PUBLIC_HOSTS})
         * @return this builder
         */
        public Builder publicHosts(boolean publicHosts) {
            this.publicHosts = publicHosts;
            return this;
        }

        /**
         * Sets the additional port the TLS proxy binds for HTTPS traffic inside the container, alongside
         * the public Floci Azure port. Azure SDKs default to {@code https://} endpoints on the
         * conventional 443 unless an explicit port is configured.
         *
         * <p>Set to {@code 0} to disable the extra binding (e.g. when another process owns 443). When
         * equal to the main Floci Azure port only a single listener is started.
         *
         * @param httpsPort the additional HTTPS port (default {@value DEFAULT_HTTPS_PORT})
         * @return this builder
         */
        public Builder httpsPort(int httpsPort) {
            this.httpsPort = httpsPort;
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

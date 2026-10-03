package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

/**
 * Network-related configuration for the Floci server, such as security-group enforcement for the
 * Docker containers Floci launches.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * NetworkConfig config = NetworkConfig.builder()
 *     .securityGroupEnforcementEnabled(true)
 *     .build();
 * }</pre>
 */
public class NetworkConfig {

    private static final boolean DEFAULT_SECURITY_GROUP_ENFORCEMENT_ENABLED = false;
    private static final String DEFAULT_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE = "floci/network-helper:local";

    private final boolean securityGroupEnforcementEnabled;
    private final String securityGroupEnforcementHelperImage;

    private NetworkConfig(Builder builder) {
        this.securityGroupEnforcementEnabled = builder.securityGroupEnforcementEnabled;
        this.securityGroupEnforcementHelperImage = builder.securityGroupEnforcementHelperImage;
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
     * Returns whether traffic of Docker-backed EC2 instances and ECS {@code awsvpc} tasks is filtered by
     * their attached security groups.
     *
     * @return {@code true} if security-group enforcement is enabled
     */
    public boolean isSecurityGroupEnforcementEnabled() {
        return securityGroupEnforcementEnabled;
    }

    /**
     * Returns the Linux helper image (containing nftables) used to enforce security groups.
     *
     * @return the helper image (default {@value DEFAULT_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE})
     */
    public String getSecurityGroupEnforcementHelperImage() {
        return securityGroupEnforcementHelperImage;
    }

    /**
     * Applies this network configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_ENABLED", String.valueOf(securityGroupEnforcementEnabled));
        container.withEnv("FLOCI_NETWORK_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE", securityGroupEnforcementHelperImage);
    }

    /**
     * Builder for {@link NetworkConfig}.
     */
    public static class Builder {

        private boolean securityGroupEnforcementEnabled = DEFAULT_SECURITY_GROUP_ENFORCEMENT_ENABLED;
        private String securityGroupEnforcementHelperImage = DEFAULT_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE;

        private Builder() {
            // Allow instantiation only via NetworkConfig.builder()
        }

        private Builder(NetworkConfig instance) {
            this.securityGroupEnforcementEnabled = instance.securityGroupEnforcementEnabled;
            this.securityGroupEnforcementHelperImage = instance.securityGroupEnforcementHelperImage;
        }

        /**
         * Sets whether traffic of Docker-backed EC2 instances and ECS {@code awsvpc} tasks is filtered by
         * their attached security groups. Requires a rootful Linux Docker daemon with nftables support;
         * mock mode stays a control-plane simulation and does not filter packets.
         *
         * @param securityGroupEnforcementEnabled {@code true} to enable security-group enforcement
         *                                        (default {@value DEFAULT_SECURITY_GROUP_ENFORCEMENT_ENABLED})
         * @return this builder
         */
        public Builder securityGroupEnforcementEnabled(boolean securityGroupEnforcementEnabled) {
            this.securityGroupEnforcementEnabled = securityGroupEnforcementEnabled;
            return this;
        }

        /**
         * Sets the Linux helper image (containing nftables) used to enforce security groups. Floci builds
         * the default image locally when it is missing.
         *
         * @param securityGroupEnforcementHelperImage the helper image
         *                                            (default {@value DEFAULT_SECURITY_GROUP_ENFORCEMENT_HELPER_IMAGE})
         * @return this builder
         */
        public Builder securityGroupEnforcementHelperImage(String securityGroupEnforcementHelperImage) {
            this.securityGroupEnforcementHelperImage = securityGroupEnforcementHelperImage;
            return this;
        }

        /**
         * Creates an immutable {@link NetworkConfig} from this builder.
         *
         * @return the network configuration
         */
        public NetworkConfig build() {
            return new NetworkConfig(this);
        }
    }
}

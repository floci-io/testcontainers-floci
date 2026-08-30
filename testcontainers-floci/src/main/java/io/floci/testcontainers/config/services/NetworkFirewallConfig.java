package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Network Firewall-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * NetworkFirewallConfig config = NetworkFirewallConfig.builder()
 *     .build();
 * }</pre>
 */
public class NetworkFirewallConfig extends AbstractServiceConfig<NetworkFirewallConfig.Builder> {

    private NetworkFirewallConfig(Builder builder) {
        super(builder.enabled);
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

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_NETWORKFIREWALL_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link NetworkFirewallConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, NetworkFirewallConfig> {

        private Builder() {
            // Allow instantiation only via NetworkFirewallConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link NetworkFirewallConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(NetworkFirewallConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link NetworkFirewallConfig} from this builder.
         *
         * @return the Network Firewall configuration
         */
        @Override
        public NetworkFirewallConfig build() {
            return new NetworkFirewallConfig(this);
        }
    }
}

package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for X-Ray-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * XrayConfig config = XrayConfig.builder()
 *     .build();
 * }</pre>
 */
public class XrayConfig extends AbstractServiceConfig<XrayConfig.Builder> {

    private XrayConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_SERVICES_XRAY_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link XrayConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, XrayConfig> {

        private Builder() {
            // Allow instantiation only via XrayConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link XrayConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(XrayConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link XrayConfig} from this builder.
         *
         * @return the X-Ray configuration
         */
        @Override
        public XrayConfig build() {
            return new XrayConfig(this);
        }
    }
}

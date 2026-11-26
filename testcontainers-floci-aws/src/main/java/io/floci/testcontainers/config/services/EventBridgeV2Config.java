package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EventBridge V2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EventBridgeV2Config config = EventBridgeV2Config.builder()
 *     .build();
 * }</pre>
 */
public class EventBridgeV2Config extends AbstractServiceConfig<EventBridgeV2Config.Builder> {

    private EventBridgeV2Config(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_EVENTBRIDGEV2_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link EventBridgeV2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EventBridgeV2Config> {

        private Builder() {
            // Allow instantiation only via EventBridgeV2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EventBridgeV2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EventBridgeV2Config instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link EventBridgeV2Config} from this builder.
         *
         * @return the EventBridge V2 configuration
         */
        @Override
        public EventBridgeV2Config build() {
            return new EventBridgeV2Config(this);
        }
    }
}

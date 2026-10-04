package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Eventarc of Floci GCP.
 *
 * <p>Eventarc is served via REST on the main port.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * EventarcConfig config = EventarcConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class EventarcConfig extends AbstractServiceConfig<EventarcConfig.Builder> {

    private EventarcConfig(Builder builder) {
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
        container.withEnv("FLOCI_GCP_SERVICES_EVENTARC_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link EventarcConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, EventarcConfig> {

        private Builder() {
            // Allow instantiation only via EventarcConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link EventarcConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(EventarcConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link EventarcConfig} from this builder.
         *
         * @return the Eventarc configuration
         */
        @Override
        public EventarcConfig build() {
            return new EventarcConfig(this);
        }
    }
}

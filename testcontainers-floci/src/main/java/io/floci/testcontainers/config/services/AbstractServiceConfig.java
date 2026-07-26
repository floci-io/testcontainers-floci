package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Base class for Floci service configurations.
 *
 * <p>Every service configuration supports an {@link #isEnabled()} flag and can apply its
 * settings to a container via {@link #applyEnvVarsToContainer(Container)}.
 *
 * @param <B> the concrete {@link AbstractServiceConfigBuilder} subtype used to build and rebuild
 *            this configuration, allowing {@link #toBuilder()} to be implemented in a type-safe
 *            way by subclasses
 */
public abstract class AbstractServiceConfig<B extends AbstractServiceConfigBuilder<B, ?>> {

    /** Default value for the {@link #isEnabled()} flag. */
    protected static final boolean DEFAULT_ENABLED = true;

    private final boolean enabled;

    /**
     * Creates a new service configuration with the given enabled flag.
     *
     * @param enabled {@code true} to enable the service
     */
    protected AbstractServiceConfig(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether this service is enabled.
     *
     * @return {@code true} if this service is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns a new builder for this configuration, initialized with the current values of this
     * instance. Every subclass must implement this to return its own {@code Builder} type,
     * pre-populated via the builder's copy constructor.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    public abstract B toBuilder();

    /**
     * Applies this service configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
    }

    /**
     * Applies this service configuration to the given container by exposing
     * the appropriate ports.
     *
     * @param container the container to configure
     */
    public void applyExposedPortsToContainer(Container<?> container) {
    }
}

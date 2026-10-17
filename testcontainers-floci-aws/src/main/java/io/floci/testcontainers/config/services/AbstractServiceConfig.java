package io.floci.testcontainers.config.services;

/**
 * Base class for Floci service configurations.
 *
 * <p>Kept for compatibility: every AWS service configuration still extends this class, but all behaviour lives in
 * the provider-independent {@link io.floci.testcontainers.core.config.services.AbstractServiceConfig}. Custom
 * configurations should extend that class directly.
 *
 * @param <B> the concrete {@link AbstractServiceConfigBuilder} subtype used to build and rebuild
 *            this configuration, allowing {@link #toBuilder()} to be implemented in a type-safe
 *            way by subclasses
 * @deprecated use {@link io.floci.testcontainers.core.config.services.AbstractServiceConfig}; this class will be
 * removed in 3.0.0.
 */
@Deprecated
public abstract class AbstractServiceConfig<B extends AbstractServiceConfigBuilder<B, ?>>
        extends io.floci.testcontainers.core.config.services.AbstractServiceConfig<B> {

    /** Default value for the {@link #isEnabled()} flag. */
    protected static final boolean DEFAULT_ENABLED = true;

    /**
     * Creates a new service configuration with the given enabled flag.
     *
     * @param enabled {@code true} to enable the service
     */
    protected AbstractServiceConfig(boolean enabled) {
        super(enabled);
    }

    /**
     * Creates a new service configuration with the enabled flag of the given builder.
     *
     * @param builder the builder to take the enabled flag from
     */
    protected AbstractServiceConfig(io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder<?, ?> builder) {
        super(builder);
    }

    /**
     * Returns a new builder for this configuration, initialized with the current values of this
     * instance. Every subclass must implement this to return its own {@code Builder} type,
     * pre-populated via the builder's copy constructor.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    @Override
    public abstract B toBuilder();
}

package io.floci.testcontainers.config.services;

/**
 * Base class for builders of {@link AbstractServiceConfig} subclasses.
 *
 * <p>Kept for compatibility: every AWS service configuration builder still extends this class, but all behaviour
 * lives in the provider-independent
 * {@link io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder}. Custom builders should extend
 * that class directly.
 *
 * @param <SELF> the concrete builder subtype, used to support a fluent, self-typed API in subclasses
 * @param <C>    the concrete {@link AbstractServiceConfig} subtype built by this builder, used as
 *               the return type of {@link #build()}
 * @deprecated use {@link io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder}; this class will
 * be removed in 3.0.0.
 */
@Deprecated
public abstract class AbstractServiceConfigBuilder<
        SELF extends AbstractServiceConfigBuilder<SELF, C>,
        C extends AbstractServiceConfig<SELF>>
        extends io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder<SELF, C> {

    /**
     * Creates a new builder with default values.
     */
    protected AbstractServiceConfigBuilder() {
    }

    /**
     * Creates a new builder initialized with the values of the given configuration instance.
     *
     * @param instance the configuration instance to copy values from
     */
    protected AbstractServiceConfigBuilder(C instance) {
        super(instance);
    }

    /**
     * Enables or disables the service.
     *
     * @param enabled {@code true} to enable, {@code false} to disable (default {@value AbstractServiceConfig#DEFAULT_ENABLED})
     * @return this builder
     */
    @Override
    public SELF enabled(boolean enabled) {
        return super.enabled(enabled);
    }

    /**
     * Creates an immutable configuration instance from this builder. Every subclass must
     * implement this to return its own concrete {@link AbstractServiceConfig} type.
     *
     * @return the configuration built from this builder's values
     */
    @Override
    public abstract C build();
}

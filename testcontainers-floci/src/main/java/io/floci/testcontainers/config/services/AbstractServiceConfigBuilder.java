package io.floci.testcontainers.config.services;

/**
 * Base class for builders of {@link AbstractServiceConfig} subclasses.
 *
 * <p>Provides shared handling of the {@link AbstractServiceConfig#isEnabled() enabled} flag,
 * including pre-populating a new builder from the values of an existing configuration instance
 * (see {@link AbstractServiceConfig#toBuilder()}).
 *
 * @param <SELF> the concrete builder subtype, used to support a fluent, self-typed API in subclasses
 * @param <C>    the concrete {@link AbstractServiceConfig} subtype built by this builder, used as
 *               the return type of {@link #build()}
 */
public abstract class AbstractServiceConfigBuilder<
        SELF extends AbstractServiceConfigBuilder<SELF, C>,
        C extends AbstractServiceConfig<SELF>> {

    protected boolean enabled = AbstractServiceConfig.DEFAULT_ENABLED;

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
        this.enabled = instance.isEnabled();
    }

    /**
     * Enables or disables the service.
     *
     * @param enabled {@code true} to enable, {@code false} to disable (default {@value AbstractServiceConfig#DEFAULT_ENABLED})
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    public SELF enabled(boolean enabled) {
        this.enabled = enabled;
        return (SELF) this;
    }

    /**
     * Creates an immutable configuration instance from this builder. Every subclass must
     * implement this to return its own concrete {@link AbstractServiceConfig} type.
     *
     * @return the configuration built from this builder's values
     */
    public abstract C build();
}

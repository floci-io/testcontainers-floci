package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for ACM-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AcmConfig config = AcmConfig.builder()
 *     .validationWaitSeconds(5)
 *     .build();
 * }</pre>
 */
public class AcmConfig extends AbstractServiceConfig<AcmConfig.Builder> {

    private static final int DEFAULT_VALIDATION_WAIT_SECONDS = 0;

    private final int validationWaitSeconds;

    private AcmConfig(Builder builder) {
        super(builder.enabled);
        this.validationWaitSeconds = builder.validationWaitSeconds;
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

    /**
     * Returns the validation wait time in seconds.
     *
     * @return the validation wait time in seconds
     */
    public int getValidationWaitSeconds() {
        return validationWaitSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_ACM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_ACM_VALIDATION_WAIT_SECONDS", String.valueOf(validationWaitSeconds));
        }
    }

    /**
     * Builder for {@link AcmConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, AcmConfig> {

        private int validationWaitSeconds = DEFAULT_VALIDATION_WAIT_SECONDS;

        private Builder() {
            // Allow instantiation only via AcmConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link AcmConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(AcmConfig instance) {
            super(instance);
            this.validationWaitSeconds = instance.getValidationWaitSeconds();
        }

        /**
         * Sets the number of seconds to wait before transitioning from PENDING_VALIDATION to ISSUED (0 = immediate).
         *
         * @param validationWaitSeconds the number of seconds to wait before transitioning from PENDING_VALIDATION to ISSUED (0 = immediate) (default {@value DEFAULT_VALIDATION_WAIT_SECONDS})
         * @return this builder
         */
        public Builder validationWaitSeconds(int validationWaitSeconds) {
            this.validationWaitSeconds = validationWaitSeconds;
            return this;
        }

        /**
         * Creates an immutable {@link AcmConfig} from this builder.
         *
         * @return the ACM configuration
         */
        @Override
        public AcmConfig build() {
            return new AcmConfig(this);
        }
    }
}

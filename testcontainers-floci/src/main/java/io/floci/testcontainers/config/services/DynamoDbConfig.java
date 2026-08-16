package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for DynamoDB-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * DynamoDbConfig config = DynamoDbConfig.builder()
 *     .build();
 * }</pre>
 */
public class DynamoDbConfig extends AbstractServiceConfig<DynamoDbConfig.Builder> {


    private DynamoDbConfig(Builder builder) {
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
        container.withEnv("FLOCI_SERVICES_DYNAMODB_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link DynamoDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DynamoDbConfig> {


        private Builder() {
            // Allow instantiation only via DynamoDbConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link DynamoDbConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(DynamoDbConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link DynamoDbConfig} from this builder.
         *
         * @return the DynamoDB configuration
         */
        @Override
        public DynamoDbConfig build() {
            return new DynamoDbConfig(this);
        }
    }
}

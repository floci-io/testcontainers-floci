package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Kinesis-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * KinesisConfig config = KinesisConfig.builder()
 *     .build();
 * }</pre>
 */
public class KinesisConfig extends AbstractServiceConfig<KinesisConfig.Builder> {

    private static final long DEFAULT_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS = 300000L;

    private final long listShardsNextTokenTtlMillis;

    private KinesisConfig(Builder builder) {
        super(builder.enabled);
        this.listShardsNextTokenTtlMillis = builder.listShardsNextTokenTtlMillis;
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
     * Returns the lifetime, in milliseconds, of a ListShards {@code NextToken}.
     *
     * <p>AWS expires these tokens 300000 milliseconds after they are issued; lowering it lets tests exercise
     * the expiry path without waiting.
     *
     * @return the lifetime, in milliseconds, of a ListShards {@code NextToken}
     */
    public long getListShardsNextTokenTtlMillis() {
        return listShardsNextTokenTtlMillis;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_KINESIS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", String.valueOf(listShardsNextTokenTtlMillis));
        }
    }

    /**
     * Builder for {@link KinesisConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KinesisConfig> {

        private long listShardsNextTokenTtlMillis = DEFAULT_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS;

        private Builder() {
            // Allow instantiation only via KinesisConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link KinesisConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(KinesisConfig instance) {
            super(instance);
            this.listShardsNextTokenTtlMillis = instance.getListShardsNextTokenTtlMillis();
        }

        /**
         * Sets the lifetime, in milliseconds, of a ListShards {@code NextToken}.
         *
         * <p>AWS expires these tokens 300000 milliseconds after they are issued; lowering it lets tests
         * exercise the expiry path without waiting.
         *
         * @param listShardsNextTokenTtlMillis the lifetime, in milliseconds, of a ListShards {@code NextToken} (default {@value DEFAULT_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS})
         * @return this builder
         */
        public Builder listShardsNextTokenTtlMillis(long listShardsNextTokenTtlMillis) {
            this.listShardsNextTokenTtlMillis = listShardsNextTokenTtlMillis;
            return this;
        }

        /**
         * Creates an immutable {@link KinesisConfig} from this builder.
         *
         * @return the Kinesis configuration
         */
        @Override
        public KinesisConfig build() {
            return new KinesisConfig(this);
        }
    }
}

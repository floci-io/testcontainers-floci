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
    private static final long DEFAULT_CREATING_DELAY_MILLIS = 0L;
    private static final long DEFAULT_UPDATING_DELAY_MILLIS = 0L;

    private final long listShardsNextTokenTtlMillis;
    private final long creatingDelayMillis;
    private final long updatingDelayMillis;

    private KinesisConfig(Builder builder) {
        super(builder);
        this.listShardsNextTokenTtlMillis = builder.listShardsNextTokenTtlMillis;
        this.creatingDelayMillis = builder.creatingDelayMillis;
        this.updatingDelayMillis = builder.updatingDelayMillis;
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

    /**
     * Returns the time, in milliseconds, a new stream stays {@code CREATING} before it becomes {@code ACTIVE}.
     *
     * @return the time, in milliseconds, a new stream stays {@code CREATING} before it becomes {@code ACTIVE}
     */
    public long getCreatingDelayMillis() {
        return creatingDelayMillis;
    }

    /**
     * Returns the time, in milliseconds, an updated stream stays {@code UPDATING} before it becomes {@code ACTIVE} again.
     *
     * @return the time, in milliseconds, an updated stream stays {@code UPDATING} before it becomes {@code ACTIVE} again
     */
    public long getUpdatingDelayMillis() {
        return updatingDelayMillis;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_KINESIS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_KINESIS_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS", String.valueOf(listShardsNextTokenTtlMillis));
            container.withEnv("FLOCI_SERVICES_KINESIS_CREATING_DELAY_MILLIS", String.valueOf(creatingDelayMillis));
            container.withEnv("FLOCI_SERVICES_KINESIS_UPDATING_DELAY_MILLIS", String.valueOf(updatingDelayMillis));
        }
    }

    /**
     * Builder for {@link KinesisConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, KinesisConfig> {

        private long listShardsNextTokenTtlMillis = DEFAULT_LIST_SHARDS_NEXT_TOKEN_TTL_MILLIS;
        private long creatingDelayMillis = DEFAULT_CREATING_DELAY_MILLIS;
        private long updatingDelayMillis = DEFAULT_UPDATING_DELAY_MILLIS;

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
            this.creatingDelayMillis = instance.getCreatingDelayMillis();
            this.updatingDelayMillis = instance.getUpdatingDelayMillis();
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
         * Sets the time, in milliseconds, a new stream stays {@code CREATING} before it becomes {@code ACTIVE}.
         *
         * @param creatingDelayMillis the time, in milliseconds, a new stream stays {@code CREATING} before it becomes {@code ACTIVE} (default {@value DEFAULT_CREATING_DELAY_MILLIS})
         * @return this builder
         */
        public Builder creatingDelayMillis(long creatingDelayMillis) {
            this.creatingDelayMillis = creatingDelayMillis;
            return this;
        }

        /**
         * Sets the time, in milliseconds, an updated stream stays {@code UPDATING} before it becomes {@code ACTIVE} again.
         *
         * @param updatingDelayMillis the time, in milliseconds, an updated stream stays {@code UPDATING} before it becomes {@code ACTIVE} again (default {@value DEFAULT_UPDATING_DELAY_MILLIS})
         * @return this builder
         */
        public Builder updatingDelayMillis(long updatingDelayMillis) {
            this.updatingDelayMillis = updatingDelayMillis;
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

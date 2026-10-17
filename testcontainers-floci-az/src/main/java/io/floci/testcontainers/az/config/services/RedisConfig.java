package io.floci.testcontainers.az.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for Azure Cache for Redis of Floci Azure.
 *
 * <p>Each cache is backed by a Valkey container that floci-az publishes on the Docker host.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * RedisConfig config = RedisConfig.builder()
 *     .mocked(true)
 *     .defaultImage("redis:7-alpine")
 *     .portRange(16379, 5)
 *     .tlsEnabled(true)
 *     .build();
 * }</pre>
 */
public class RedisConfig extends AbstractServiceConfig<RedisConfig.Builder> {

    private static final boolean DEFAULT_MOCKED = false;
    private static final String DEFAULT_DEFAULT_IMAGE = "valkey/valkey:8-alpine";
    private static final int DEFAULT_BASE_PORT = 6379;
    private static final int DEFAULT_PORTS_COUNT = 10;
    private static final String DEFAULT_MAX_MEMORY = "256mb";
    private static final boolean DEFAULT_TLS_ENABLED = false;

    private final boolean mocked;
    private final String defaultImage;
    private final int basePort;
    private final int portsCount;
    private final String maxMemory;
    private final boolean tlsEnabled;

    private RedisConfig(Builder builder) {
        super(builder);
        this.mocked = builder.mocked;
        this.defaultImage = builder.defaultImage;
        this.basePort = builder.basePort;
        this.portsCount = builder.portsCount;
        this.maxMemory = builder.maxMemory;
        this.tlsEnabled = builder.tlsEnabled;
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
     * Returns whether no Redis container is started; caches transition immediately to
     * {@code Succeeded} with {@code hostName=localhost}.
     *
     * @return {@code true} if the service is mocked
     */
    public boolean isMocked() {
        return mocked;
    }

    /**
     * Returns the Docker image backing each cache.
     *
     * @return the Docker image
     */
    public String getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the first port of the host port range of the Redis instances.
     *
     * @return the base port
     */
    public int getBasePort() {
        return basePort;
    }

    /**
     * Returns the number of ports of the host port range of the Redis instances, starting from {@link #getBasePort()}.
     *
     * @return the number of ports
     */
    public int getPortsCount() {
        return portsCount;
    }

    /**
     * Returns the last port of the host port range of the Redis instances.
     *
     * @return the maximum port
     */
    public int getMaxPort() {
        return basePort + portsCount - 1;
    }

    /**
     * Returns the maximum memory per Redis instance (e.g. {@code 256mb}).
     *
     * @return the maximum memory
     */
    public String getMaxMemory() {
        return maxMemory;
    }

    /**
     * Returns whether each cache also serves TLS on the container's port 6380, published on a second
     * host port from the port range and reported as {@code sslPort}.
     *
     * @return {@code true} if TLS is enabled for the caches
     */
    public boolean isTlsEnabled() {
        return tlsEnabled;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_SERVICES_REDIS_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_MOCKED", String.valueOf(mocked));
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_DEFAULT_IMAGE", defaultImage);
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_BASE_PORT", String.valueOf(basePort));
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_MAX_PORT", String.valueOf(getMaxPort()));
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_MAX_MEMORY", maxMemory);
            container.withEnv("FLOCI_AZ_SERVICES_REDIS_TLS_ENABLED", String.valueOf(tlsEnabled));
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mocked;
    }

    /**
     * Builder for {@link RedisConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, RedisConfig> {

        private boolean mocked = DEFAULT_MOCKED;
        private String defaultImage = DEFAULT_DEFAULT_IMAGE;
        private int basePort = DEFAULT_BASE_PORT;
        private int portsCount = DEFAULT_PORTS_COUNT;
        private String maxMemory = DEFAULT_MAX_MEMORY;
        private boolean tlsEnabled = DEFAULT_TLS_ENABLED;

        private Builder() {
            // Allow instantiation only via RedisConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link RedisConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(RedisConfig instance) {
            super(instance);
            this.mocked = instance.mocked;
            this.defaultImage = instance.defaultImage;
            this.basePort = instance.basePort;
            this.portsCount = instance.portsCount;
            this.maxMemory = instance.maxMemory;
            this.tlsEnabled = instance.tlsEnabled;
        }

        /**
         * Sets whether no Redis container is started; caches transition immediately to {@code Succeeded}
         * with {@code hostName=localhost}. Useful for tests without Docker.
         *
         * @param mocked {@code true} to mock the service without Docker (default {@value DEFAULT_MOCKED})
         * @return this builder
         */
        public Builder mocked(boolean mocked) {
            this.mocked = mocked;
            return this;
        }

        /**
         * Sets the Docker image backing each cache.
         *
         * @param defaultImage the Docker image (default {@value DEFAULT_DEFAULT_IMAGE})
         * @return this builder
         */
        public Builder defaultImage(String defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        /**
         * Sets the host port range the Redis instances are published on. Floci itself allows
         * 6379-6399; this module defaults to 10 ports, i.e. 10 concurrent caches.
         *
         * @param basePort the first port of the range (default {@value DEFAULT_BASE_PORT})
         * @param amount   the number of ports in the range (default {@value DEFAULT_PORTS_COUNT})
         * @return this builder
         */
        public Builder portRange(int basePort, int amount) {
            this.basePort = basePort;
            this.portsCount = amount;
            return this;
        }

        /**
         * Sets the maximum memory per Redis instance (e.g. {@code 256mb}, {@code 1gb}).
         *
         * @param maxMemory the maximum memory (default {@value DEFAULT_MAX_MEMORY})
         * @return this builder
         */
        public Builder maxMemory(String maxMemory) {
            this.maxMemory = maxMemory;
            return this;
        }

        /**
         * Sets whether each cache also serves TLS on the container's port 6380, published on a second
         * host port from the port range and reported as {@code sslPort}. While enabled, every cache takes
         * two host ports, so the port range fits only half as many caches.
         *
         * @param tlsEnabled {@code true} to enable TLS for the caches (default {@value DEFAULT_TLS_ENABLED})
         * @return this builder
         */
        public Builder tlsEnabled(boolean tlsEnabled) {
            this.tlsEnabled = tlsEnabled;
            return this;
        }

        /**
         * Creates an immutable {@link RedisConfig} from this builder.
         *
         * @return the Redis configuration
         */
        @Override
        public RedisConfig build() {
            return new RedisConfig(this);
        }
    }
}

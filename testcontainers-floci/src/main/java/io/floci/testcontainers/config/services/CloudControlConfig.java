package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for Cloud Control API-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudControlConfig config = CloudControlConfig.builder()
 *     .createWorkerThreads(8)
 *     .createQueueCapacity(128)
 *     .build();
 * }</pre>
 */
public class CloudControlConfig extends AbstractServiceConfig<CloudControlConfig.Builder> {

    private static final int DEFAULT_CREATE_WORKER_THREADS = 4;
    private static final int DEFAULT_CREATE_QUEUE_CAPACITY = 64;

    private final int createWorkerThreads;
    private final int createQueueCapacity;

    private CloudControlConfig(Builder builder) {
        super(builder.enabled);
        this.createWorkerThreads = builder.createWorkerThreads;
        this.createQueueCapacity = builder.createQueueCapacity;
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
     * Returns the number of worker threads that run CreateResource provisioning.
     *
     * @return the number of create worker threads
     */
    public int getCreateWorkerThreads() {
        return createWorkerThreads;
    }

    /**
     * Returns the number of CreateResource requests that may wait for a worker before new creates
     * are refused with a {@code ThrottlingException}.
     *
     * @return the create queue capacity
     */
    public int getCreateQueueCapacity() {
        return createQueueCapacity;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDCONTROL_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDCONTROL_CREATE_WORKER_THREADS", String.valueOf(createWorkerThreads));
            container.withEnv("FLOCI_SERVICES_CLOUDCONTROL_CREATE_QUEUE_CAPACITY", String.valueOf(createQueueCapacity));
        }
    }

    /**
     * Builder for {@link CloudControlConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudControlConfig> {

        private int createWorkerThreads = DEFAULT_CREATE_WORKER_THREADS;
        private int createQueueCapacity = DEFAULT_CREATE_QUEUE_CAPACITY;

        private Builder() {
            // Allow instantiation only via CloudControlConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudControlConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudControlConfig instance) {
            super(instance);
            this.createWorkerThreads = instance.getCreateWorkerThreads();
            this.createQueueCapacity = instance.getCreateQueueCapacity();
        }

        /**
         * Sets the number of worker threads that run CreateResource provisioning.
         *
         * @param createWorkerThreads the number of create worker threads (default {@value DEFAULT_CREATE_WORKER_THREADS})
         * @return this builder
         */
        public Builder createWorkerThreads(int createWorkerThreads) {
            this.createWorkerThreads = createWorkerThreads;
            return this;
        }

        /**
         * Sets the number of CreateResource requests that may wait for a worker. Once the queue is
         * full, new creates are refused with a {@code ThrottlingException}.
         *
         * @param createQueueCapacity the create queue capacity (default {@value DEFAULT_CREATE_QUEUE_CAPACITY})
         * @return this builder
         */
        public Builder createQueueCapacity(int createQueueCapacity) {
            this.createQueueCapacity = createQueueCapacity;
            return this;
        }

        /**
         * Creates an immutable {@link CloudControlConfig} from this builder.
         *
         * @return the Cloud Control API configuration
         */
        @Override
        public CloudControlConfig build() {
            return new CloudControlConfig(this);
        }
    }
}

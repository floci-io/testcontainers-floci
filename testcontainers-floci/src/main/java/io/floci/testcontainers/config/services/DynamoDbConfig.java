package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

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

    private static final int DEFAULT_VECTOR_INDEX_ALLOCATION_SECONDS = 4;
    private static final int DEFAULT_VECTOR_INDEX_BACKFILL_SECONDS = 10;
    private static final String DEFAULT_BACKEND = "native";
    private static final int DEFAULT_LOCAL_CONNECT_TIMEOUT_SECONDS = 2;
    private static final int DEFAULT_LOCAL_REQUEST_TIMEOUT_SECONDS = 10;

    private final int vectorIndexAllocationSeconds;
    private final int vectorIndexBackfillSeconds;
    private final String backend;
    private final String localEndpoint;
    private final int localConnectTimeoutSeconds;
    private final int localRequestTimeoutSeconds;

    private DynamoDbConfig(Builder builder) {
        super(builder.enabled);
        this.vectorIndexAllocationSeconds = builder.vectorIndexAllocationSeconds;
        this.vectorIndexBackfillSeconds = builder.vectorIndexBackfillSeconds;
        this.backend = builder.backend;
        this.localEndpoint = builder.localEndpoint;
        this.localConnectTimeoutSeconds = builder.localConnectTimeoutSeconds;
        this.localRequestTimeoutSeconds = builder.localRequestTimeoutSeconds;
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
     * Returns the number of seconds a vector index added by UpdateTable spends in the resource allocation
     * phase.
     *
     * <p>During this phase the table reads {@code UPDATING}, the index reads {@code CREATING} with
     * {@code Backfilling} false, and a delete of that index is refused.
     *
     * @return the number of seconds a vector index added by UpdateTable spends in the resource allocation phase
     */
    public int getVectorIndexAllocationSeconds() {
        return vectorIndexAllocationSeconds;
    }

    /**
     * Returns the number of seconds a vector index added by UpdateTable then spends backfilling.
     *
     * <p>During this phase the table reads {@code ACTIVE} and the index reads {@code CREATING} with
     * {@code Backfilling} true. A vector index created by CreateTable skips both phases and is {@code ACTIVE}
     * at once.
     *
     * @return the number of seconds a vector index added by UpdateTable then spends backfilling
     */
    public int getVectorIndexBackfillSeconds() {
        return vectorIndexBackfillSeconds;
    }

    /**
     * Returns the engine behind the DynamoDB API: {@code native} or {@code local}.
     *
     * <p>With {@code local}, Floci forwards requests to the DynamoDB Local instance configured via the local
     * endpoint.
     *
     * @return the engine behind the DynamoDB API: {@code native} or {@code local}
     */
    public String getBackend() {
        return backend;
    }

    /**
     * Returns the DynamoDB Local base URL.
     *
     * <p>Required when the backend is {@code local}.
     *
     * @return the DynamoDB Local base URL, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getLocalEndpoint() {
        return Optional.ofNullable(localEndpoint);
    }

    /**
     * Returns the connect timeout in seconds for requests to the DynamoDB Local backend.
     *
     * @return the connect timeout in seconds for requests to the DynamoDB Local backend
     */
    public int getLocalConnectTimeoutSeconds() {
        return localConnectTimeoutSeconds;
    }

    /**
     * Returns the request timeout in seconds for requests to the DynamoDB Local backend.
     *
     * @return the request timeout in seconds for requests to the DynamoDB Local backend
     */
    public int getLocalRequestTimeoutSeconds() {
        return localRequestTimeoutSeconds;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_DYNAMODB_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_ALLOCATION_SECONDS", String.valueOf(vectorIndexAllocationSeconds));
            container.withEnv("FLOCI_SERVICES_DYNAMODB_VECTOR_INDEX_BACKFILL_SECONDS", String.valueOf(vectorIndexBackfillSeconds));
            container.withEnv("FLOCI_SERVICES_DYNAMODB_BACKEND", backend);

            if (localEndpoint != null) {
                container.withEnv("FLOCI_SERVICES_DYNAMODB_LOCAL_ENDPOINT", localEndpoint);
            }

            container.withEnv("FLOCI_SERVICES_DYNAMODB_LOCAL_CONNECT_TIMEOUT_SECONDS", String.valueOf(localConnectTimeoutSeconds));
            container.withEnv("FLOCI_SERVICES_DYNAMODB_LOCAL_REQUEST_TIMEOUT_SECONDS", String.valueOf(localRequestTimeoutSeconds));
        }
    }

    /**
     * Builder for {@link DynamoDbConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, DynamoDbConfig> {

        private int vectorIndexAllocationSeconds = DEFAULT_VECTOR_INDEX_ALLOCATION_SECONDS;
        private int vectorIndexBackfillSeconds = DEFAULT_VECTOR_INDEX_BACKFILL_SECONDS;
        private String backend = DEFAULT_BACKEND;
        private String localEndpoint;
        private int localConnectTimeoutSeconds = DEFAULT_LOCAL_CONNECT_TIMEOUT_SECONDS;
        private int localRequestTimeoutSeconds = DEFAULT_LOCAL_REQUEST_TIMEOUT_SECONDS;

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
            this.vectorIndexAllocationSeconds = instance.getVectorIndexAllocationSeconds();
            this.vectorIndexBackfillSeconds = instance.getVectorIndexBackfillSeconds();
            this.backend = instance.getBackend();
            this.localEndpoint = instance.getLocalEndpoint().orElse(null);
            this.localConnectTimeoutSeconds = instance.getLocalConnectTimeoutSeconds();
            this.localRequestTimeoutSeconds = instance.getLocalRequestTimeoutSeconds();
        }

        /**
         * Sets the number of seconds a vector index added by UpdateTable spends in the resource allocation
         * phase.
         *
         * <p>During this phase the table reads {@code UPDATING}, the index reads {@code CREATING} with
         * {@code Backfilling} false, and a delete of that index is refused.
         *
         * @param vectorIndexAllocationSeconds the number of seconds a vector index added by UpdateTable spends in the resource allocation phase (default {@value DEFAULT_VECTOR_INDEX_ALLOCATION_SECONDS})
         * @return this builder
         */
        public Builder vectorIndexAllocationSeconds(int vectorIndexAllocationSeconds) {
            this.vectorIndexAllocationSeconds = vectorIndexAllocationSeconds;
            return this;
        }

        /**
         * Sets the number of seconds a vector index added by UpdateTable then spends backfilling.
         *
         * <p>During this phase the table reads {@code ACTIVE} and the index reads {@code CREATING} with
         * {@code Backfilling} true. A vector index created by CreateTable skips both phases and is
         * {@code ACTIVE} at once.
         *
         * @param vectorIndexBackfillSeconds the number of seconds a vector index added by UpdateTable then spends backfilling (default {@value DEFAULT_VECTOR_INDEX_BACKFILL_SECONDS})
         * @return this builder
         */
        public Builder vectorIndexBackfillSeconds(int vectorIndexBackfillSeconds) {
            this.vectorIndexBackfillSeconds = vectorIndexBackfillSeconds;
            return this;
        }

        /**
         * Sets the engine behind the DynamoDB API: {@code native} or {@code local}.
         *
         * <p>With {@code local}, Floci forwards requests to the DynamoDB Local instance configured via the
         * local endpoint.
         *
         * @param backend the engine behind the DynamoDB API: {@code native} or {@code local} (default {@value DEFAULT_BACKEND})
         * @return this builder
         */
        public Builder backend(String backend) {
            this.backend = backend;
            return this;
        }

        /**
         * Sets the DynamoDB Local base URL.
         *
         * <p>Required when the backend is {@code local}.
         *
         * @param localEndpoint the DynamoDB Local base URL, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder localEndpoint(String localEndpoint) {
            this.localEndpoint = localEndpoint;
            return this;
        }

        /**
         * Sets the connect timeout in seconds for requests to the DynamoDB Local backend.
         *
         * @param localConnectTimeoutSeconds the connect timeout in seconds for requests to the DynamoDB Local backend (default {@value DEFAULT_LOCAL_CONNECT_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder localConnectTimeoutSeconds(int localConnectTimeoutSeconds) {
            this.localConnectTimeoutSeconds = localConnectTimeoutSeconds;
            return this;
        }

        /**
         * Sets the request timeout in seconds for requests to the DynamoDB Local backend.
         *
         * @param localRequestTimeoutSeconds the request timeout in seconds for requests to the DynamoDB Local backend (default {@value DEFAULT_LOCAL_REQUEST_TIMEOUT_SECONDS})
         * @return this builder
         */
        public Builder localRequestTimeoutSeconds(int localRequestTimeoutSeconds) {
            this.localRequestTimeoutSeconds = localRequestTimeoutSeconds;
            return this;
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

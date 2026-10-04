package io.floci.testcontainers.oci.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

/**
 * Configuration for OCI Object Storage of Floci OCI.
 *
 * <p>Buckets and objects are served under {@code /n/{namespace}/b/...}; the namespace is the one configured via {@code FlociOciContainer#withNamespace(String)}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ObjectStorageConfig config = ObjectStorageConfig.builder()
 *     .enabled(false)
 *     .build();
 * }</pre>
 */
public class ObjectStorageConfig extends AbstractServiceConfig<ObjectStorageConfig.Builder> {

    private ObjectStorageConfig(Builder builder) {
        super(builder);
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
        container.withEnv("FLOCI_OCI_SERVICES_OBJECTSTORAGE_ENABLED", String.valueOf(isEnabled()));
    }

    /**
     * Builder for {@link ObjectStorageConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, ObjectStorageConfig> {

        private Builder() {
            // Allow instantiation only via ObjectStorageConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link ObjectStorageConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(ObjectStorageConfig instance) {
            super(instance);
        }

        /**
         * Creates an immutable {@link ObjectStorageConfig} from this builder.
         *
         * @return the OCI Object Storage configuration
         */
        @Override
        public ObjectStorageConfig build() {
            return new ObjectStorageConfig(this);
        }
    }
}

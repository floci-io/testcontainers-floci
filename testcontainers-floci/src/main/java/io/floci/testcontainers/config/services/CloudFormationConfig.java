package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for CloudFormation-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CloudFormationConfig config = CloudFormationConfig.builder()
 *     .deletedStackRetentionSeconds(60)
 *     .build();
 * }</pre>
 */
public class CloudFormationConfig extends AbstractServiceConfig<CloudFormationConfig.Builder> {

    private static final long DEFAULT_DELETED_STACK_RETENTION_SECONDS = 30L;
    private static final boolean DEFAULT_ALLOW_STUB_LAMBDA_CODE = false;
    private static final boolean DEFAULT_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES = true;

    private final long deletedStackRetentionSeconds;
    private final boolean allowStubLambdaCode;
    private final boolean allowStubUnsupportedResourceTypes;

    private CloudFormationConfig(Builder builder) {
        super(builder.enabled);
        this.deletedStackRetentionSeconds = builder.deletedStackRetentionSeconds;
        this.allowStubLambdaCode = builder.allowStubLambdaCode;
        this.allowStubUnsupportedResourceTypes = builder.allowStubUnsupportedResourceTypes;
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
     * Returns how long deleted stacks are retained in seconds.
     *
     * @return the deleted stack retention period in seconds
     */
    public long getDeletedStackRetentionSeconds() {
        return deletedStackRetentionSeconds;
    }

    /**
     * Returns whether an {@code AWS::Lambda::Function} whose template names code in S3 that cannot
     * be read falls back to the built-in stub handler instead of failing the resource.
     *
     * <p>Defaults to {@code false}, matching real CloudFormation, which fails the resource and
     * rolls the stack back. When {@code true}, such a stack reports {@code CREATE_COMPLETE} while
     * serving a placeholder that returns {@code {"statusCode":200}}, so it cannot be used to
     * verify the real function.
     *
     * @return {@code true} if unreadable Lambda code falls back to the built-in stub handler
     */
    public boolean isAllowStubLambdaCode() {
        return allowStubLambdaCode;
    }

    /**
     * Returns whether a resource whose type Floci has no provisioner for may be stubbed.
     *
     * <p>A stubbed resource gets a synthetic physical id, an {@code arn:aws:stub:::} ARN attribute and
     * {@code CREATE_COMPLETE}, with a status reason saying nothing was created. Set to {@code false} to fail
     * such a resource instead, which rolls the stack back.
     *
     * @return whether a resource whose type Floci has no provisioner for may be stubbed
     */
    public boolean isAllowStubUnsupportedResourceTypes() {
        return allowStubUnsupportedResourceTypes;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_DELETED_STACK_RETENTION_SECONDS", String.valueOf(deletedStackRetentionSeconds));
            container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_LAMBDA_CODE", String.valueOf(allowStubLambdaCode));
            container.withEnv("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES", String.valueOf(allowStubUnsupportedResourceTypes));
        }
    }

    /**
     * Builder for {@link CloudFormationConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CloudFormationConfig> {

        private long deletedStackRetentionSeconds = DEFAULT_DELETED_STACK_RETENTION_SECONDS;
        private boolean allowStubLambdaCode = DEFAULT_ALLOW_STUB_LAMBDA_CODE;
        private boolean allowStubUnsupportedResourceTypes = DEFAULT_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES;

        private Builder() {
            // Allow instantiation only via CloudFormationConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CloudFormationConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CloudFormationConfig instance) {
            super(instance);
            this.deletedStackRetentionSeconds = instance.getDeletedStackRetentionSeconds();
            this.allowStubLambdaCode = instance.isAllowStubLambdaCode();
            this.allowStubUnsupportedResourceTypes = instance.isAllowStubUnsupportedResourceTypes();
        }

        /**
         * Sets how long deleted stacks are retained in seconds.
         *
         * @param deletedStackRetentionSeconds the retention period in seconds (default {@value DEFAULT_DELETED_STACK_RETENTION_SECONDS})
         * @return this builder
         */
        public Builder deletedStackRetentionSeconds(long deletedStackRetentionSeconds) {
            this.deletedStackRetentionSeconds = deletedStackRetentionSeconds;
            return this;
        }

        /**
         * Sets whether an {@code AWS::Lambda::Function} whose template names code in S3 that cannot
         * be read should fall back to the built-in stub handler instead of failing the resource.
         *
         * <p>Defaults to {@code false}, matching real CloudFormation, which fails the resource and
         * rolls the stack back. Set to {@code true} to restore the older behaviour for a stack that
         * deliberately leaves Lambda packages unbuilt — note that such a stack reports
         * {@code CREATE_COMPLETE} while serving a placeholder that returns
         * {@code {"statusCode":200}}, so it cannot be used to verify the real function.
         *
         * @param allowStubLambdaCode {@code true} to fall back to the built-in stub handler (default {@value DEFAULT_ALLOW_STUB_LAMBDA_CODE})
         * @return this builder
         */
        public Builder allowStubLambdaCode(boolean allowStubLambdaCode) {
            this.allowStubLambdaCode = allowStubLambdaCode;
            return this;
        }

        /**
         * Sets whether a resource whose type Floci has no provisioner for may be stubbed.
         *
         * <p>A stubbed resource gets a synthetic physical id, an {@code arn:aws:stub:::} ARN attribute and
         * {@code CREATE_COMPLETE}, with a status reason saying nothing was created. Set to {@code false} to
         * fail such a resource instead, which rolls the stack back.
         *
         * @param allowStubUnsupportedResourceTypes whether a resource whose type Floci has no provisioner for may be stubbed (default {@value DEFAULT_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES})
         * @return this builder
         */
        public Builder allowStubUnsupportedResourceTypes(boolean allowStubUnsupportedResourceTypes) {
            this.allowStubUnsupportedResourceTypes = allowStubUnsupportedResourceTypes;
            return this;
        }

        /**
         * Creates an immutable {@link CloudFormationConfig} from this builder.
         *
         * @return the CloudFormation configuration
         */
        @Override
        public CloudFormationConfig build() {
            return new CloudFormationConfig(this);
        }
    }
}

package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudFormationConfigTest {

    @Test
    void shouldApplyDefaultCloudFormationConfig() {
        CloudFormationConfig config = CloudFormationConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDeletedStackRetentionSeconds()).isEqualTo(30L);
        assertThat(config.isAllowStubLambdaCode()).isFalse();
    }

    @Test
    void shouldApplyCustomCloudFormationConfig() {
        CloudFormationConfig config = CloudFormationConfig.builder()
                .enabled(false)
                .deletedStackRetentionSeconds(120L)
                .allowStubLambdaCode(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDeletedStackRetentionSeconds()).isEqualTo(120L);
        assertThat(config.isAllowStubLambdaCode()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFormationConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_DELETED_STACK_RETENTION_SECONDS", "30")
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_LAMBDA_CODE", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFormationConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDFORMATION_DELETED_STACK_RETENTION_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_LAMBDA_CODE");
    }

    @Test
    void shouldApplyCustomDeletedStackRetentionSecondsEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFormationConfig.builder().deletedStackRetentionSeconds(120L).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_DELETED_STACK_RETENTION_SECONDS", "120");
    }

    @Test
    void shouldApplyCustomAllowStubLambdaCodeEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFormationConfig.builder().allowStubLambdaCode(true).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_LAMBDA_CODE", "true");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudFormationConfig config = CloudFormationConfig.builder()
                .enabled(false)
                .deletedStackRetentionSeconds(60L)
                .allowStubLambdaCode(true)
                .build();
        CloudFormationConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDeletedStackRetentionSeconds()).isEqualTo(60L);
        assertThat(copy.isAllowStubLambdaCode()).isTrue();
    }

    @Test
    void shouldApplyAllowStubUnsupportedResourceTypes() {
        CloudFormationConfig defaults = CloudFormationConfig.builder().build();
        assertThat(defaults.isAllowStubUnsupportedResourceTypes()).isEqualTo(true);

        CloudFormationConfig config = CloudFormationConfig.builder().allowStubUnsupportedResourceTypes(false).build();
        assertThat(config.isAllowStubUnsupportedResourceTypes()).isEqualTo(false);
        assertThat(config.toBuilder().build().isAllowStubUnsupportedResourceTypes()).isEqualTo(false);

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES", "false");

        GenericContainer<?> defaultContainer = genericContainer();
        defaults.applyEnvVarsToContainer(defaultContainer);
        assertThat(defaultContainer.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES", "true");

        GenericContainer<?> disabledContainer = genericContainer();
        config.toBuilder().enabled(false).build().applyEnvVarsToContainer(disabledContainer);
        assertThat(disabledContainer.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CLOUDFORMATION_ALLOW_STUB_UNSUPPORTED_RESOURCE_TYPES");
    }

}

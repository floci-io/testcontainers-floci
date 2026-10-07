package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IamConfigTest {

    @Test
    void shouldApplyDefaultIamConfig() {
        IamConfig config = IamConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getAuthorizationMode()).isEqualTo(IamConfig.AuthorizationMode.DISABLED);
        assertThat(config.getBootstrapAdminMember()).isEmpty();
    }

    @Test
    void shouldApplyCustomIamConfig() {
        IamConfig config = IamConfig.builder()
                .enabled(false)
                .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
                .bootstrapAdminMember("allAuthenticatedUsers")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getAuthorizationMode()).isEqualTo(IamConfig.AuthorizationMode.ENFORCE);
        assertThat(config.getBootstrapAdminMember()).contains("allAuthenticatedUsers");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_IAM_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_IAM_AUTHORIZATION_MODE", "disabled")
                .doesNotContainKey("FLOCI_GCP_SERVICES_IAM_BOOTSTRAP_ADMIN_MEMBER");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder()
                .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
                .bootstrapAdminMember("serviceAccount:admin@floci-local.iam.gserviceaccount.com")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_IAM_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_IAM_AUTHORIZATION_MODE", "enforce")
                .containsEntry("FLOCI_GCP_SERVICES_IAM_BOOTSTRAP_ADMIN_MEMBER",
                        "serviceAccount:admin@floci-local.iam.gserviceaccount.com");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder()
                .enabled(false)
                .bootstrapAdminMember("allUsers")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_IAM_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_IAM_AUTHORIZATION_MODE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_IAM_BOOTSTRAP_ADMIN_MEMBER");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IamConfig config = IamConfig.builder()
                .enabled(false)
                .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
                .bootstrapAdminMember("allAuthenticatedUsers")
                .build();
        IamConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getAuthorizationMode()).isEqualTo(IamConfig.AuthorizationMode.ENFORCE);
        assertThat(copy.getBootstrapAdminMember()).contains("allAuthenticatedUsers");
    }
}

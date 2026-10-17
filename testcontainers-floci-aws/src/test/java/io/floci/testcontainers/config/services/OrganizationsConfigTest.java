package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class OrganizationsConfigTest {

    @Test
    void shouldApplyDefaultOrganizationsConfig() {
        OrganizationsConfig config = OrganizationsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isScpEnforcementEnabled()).isFalse();
        assertThat(config.getManagementAccountEmail()).isNull();
    }

    @Test
    void shouldApplyCustomOrganizationsConfig() {
        OrganizationsConfig config = OrganizationsConfig.builder()
                .enabled(false)
                .scpEnforcementEnabled(true)
                .managementAccountEmail("root@example.com")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isScpEnforcementEnabled()).isTrue();
        assertThat(config.getManagementAccountEmail()).isEqualTo("root@example.com");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        OrganizationsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_SCP_ENFORCEMENT_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ORGANIZATIONS_MANAGEMENT_ACCOUNT_EMAIL");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        OrganizationsConfig.builder()
                .scpEnforcementEnabled(true)
                .managementAccountEmail("root@example.com")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_SCP_ENFORCEMENT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_MANAGEMENT_ACCOUNT_EMAIL", "root@example.com");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        OrganizationsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_ORGANIZATIONS_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_ORGANIZATIONS_SCP_ENFORCEMENT_ENABLED");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        OrganizationsConfig config = OrganizationsConfig.builder()
                .enabled(false)
                .scpEnforcementEnabled(true)
                .managementAccountEmail("root@example.com")
                .build();
        OrganizationsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isScpEnforcementEnabled()).isTrue();
        assertThat(copy.getManagementAccountEmail()).isEqualTo("root@example.com");
    }
}

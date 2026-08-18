package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class IamConfigTest {

    @Test
    void shouldApplyDefaultIamConfig() {
        IamConfig config = IamConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isEnforcementEnabled()).isFalse();
        assertThat(config.isSeedDeployerPrincipal()).isFalse();
        assertThat(config.getAccountAlias()).isEmpty();
    }

    @Test
    void shouldApplyCustomIamConfig() {
        IamConfig config = IamConfig.builder()
                .enabled(false)
                .enforcementEnabled(true)
                .seedDeployerPrincipal(true)
                .accountAlias("my-account-alias")
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isEnforcementEnabled()).isTrue();
        assertThat(config.isSeedDeployerPrincipal()).isTrue();
        assertThat(config.getAccountAlias()).contains("my-account-alias");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IAM_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_IAM_ENFORCEMENT_ENABLED", "false")
                .containsEntry("FLOCI_SERVICES_IAM_SEED_DEPLOYER_PRINCIPAL", "false")
                .doesNotContainKey("FLOCI_SERVICES_IAM_ACCOUNT_ALIAS");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder()
                .seedDeployerPrincipal(true)
                .accountAlias("my-account-alias")
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_IAM_SEED_DEPLOYER_PRINCIPAL", "true")
                .containsEntry("FLOCI_SERVICES_IAM_ACCOUNT_ALIAS", "my-account-alias");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_IAM_ENABLED", "false");
    }

    @Test
    void shouldNotApplyAccountAliasEnvVarWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        IamConfig.builder().enabled(false).accountAlias("my-account-alias").build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_IAM_ACCOUNT_ALIAS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        IamConfig config = IamConfig.builder()
                .enabled(false)
                .enforcementEnabled(true)
                .seedDeployerPrincipal(true)
                .accountAlias("my-account-alias")
                .build();
        IamConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isEnforcementEnabled()).isTrue();
        assertThat(copy.isSeedDeployerPrincipal()).isTrue();
        assertThat(copy.getAccountAlias()).contains("my-account-alias");
    }

}

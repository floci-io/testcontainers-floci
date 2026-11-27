package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class SignerConfigTest {

    @Test
    void shouldApplyDefaultSignerConfig() {
        SignerConfig config = SignerConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isMockSigningJobs()).isFalse();
    }

    @Test
    void shouldApplyCustomSignerConfig() {
        SignerConfig config = SignerConfig.builder()
                .enabled(false)
                .mockSigningJobs(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isMockSigningJobs()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SignerConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SIGNER_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SIGNER_MOCK_SIGNING_JOBS", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        SignerConfig.builder()
                .mockSigningJobs(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_SIGNER_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_SIGNER_MOCK_SIGNING_JOBS", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        SignerConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_SIGNER_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_SIGNER_MOCK_SIGNING_JOBS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        SignerConfig config = SignerConfig.builder()
                .enabled(false)
                .mockSigningJobs(true)
                .build();
        SignerConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isMockSigningJobs()).isTrue();
    }

}

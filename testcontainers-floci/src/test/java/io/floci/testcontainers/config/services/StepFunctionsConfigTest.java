package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.TransferableCopyInspector.contentCopiedTo;
import static org.testcontainers.containers.TransferableCopyInspector.pendingCopies;

class StepFunctionsConfigTest {

    private static final String MOCK_CONFIG_JSON = """
            {
              "StateMachines": {
                "sm": { "TestCases": { "happy": { "MockedResponses": {} } } }
              },
              "MockedResponses": {}
            }
            """;

    @Test
    void shouldApplyDefaultStepFunctionsConfig() {
        StepFunctionsConfig config = StepFunctionsConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isAllowPlaintextHttp()).isTrue();
        assertThat(config.getMockConfigFile()).isEmpty();
        assertThat(config.getMockConfig()).isEmpty();
    }

    @Test
    void shouldApplyCustomStepFunctionsConfig() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .allowPlaintextHttp(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isAllowPlaintextHttp()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "true");
        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", "true");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_STEPFUNCTIONS_MOCK_CONFIG_FILE");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().allowPlaintextHttp(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP", "false");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_ENABLED", "false");
        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_STEPFUNCTIONS_ALLOW_PLAINTEXT_HTTP");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .enabled(false)
                .allowPlaintextHttp(false)
                .build();
        StepFunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isAllowPlaintextHttp()).isFalse();
    }

    @Test
    void shouldUseExplicitMockConfigFilePath() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfigFile("/etc/floci/MockConfigFile.json")
                .build();

        assertThat(config.getMockConfigFile()).contains("/etc/floci/MockConfigFile.json");
        assertThat(config.getMockConfig()).isEmpty();

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_MOCK_CONFIG_FILE", "/etc/floci/MockConfigFile.json");
    }

    @Test
    void shouldGenerateRandomContainerPathForMockConfigContent() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();

        assertThat(config.getMockConfig()).contains(MOCK_CONFIG_JSON);
        assertThat(config.getMockConfigFile()).isPresent();
        assertThat(config.getMockConfigFile().orElseThrow())
                .startsWith("/tmp/floci-sfn-mock-config-")
                .endsWith(".json");
    }

    @Test
    void shouldSetMockConfigFileEnvVarForMockConfigContent() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();
        String containerPath = config.getMockConfigFile().orElseThrow();

        GenericContainer<?> container = genericContainer();
        config.applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_STEPFUNCTIONS_MOCK_CONFIG_FILE", containerPath);
    }

    @Test
    void shouldClearMockConfigContentWhenExplicitPathIsSet() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .mockConfigFile("/etc/floci/MockConfigFile.json")
                .build();

        assertThat(config.getMockConfig()).isEmpty();
        assertThat(config.getMockConfigFile()).contains("/etc/floci/MockConfigFile.json");

        GenericContainer<?> container = genericContainer();
        config.applyFileMountsToContainer(container);
        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldClearExplicitPathWhenMockConfigContentIsSet() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfigFile("/etc/floci/MockConfigFile.json")
                .mockConfig(MOCK_CONFIG_JSON)
                .build();

        assertThat(config.getMockConfig()).contains(MOCK_CONFIG_JSON);
        assertThat(config.getMockConfigFile().orElseThrow()).startsWith("/tmp/floci-sfn-mock-config-");
    }

    @Test
    void shouldNotRegenerateMockConfigFileOnUnrelatedToBuilderChange() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();
        String originalPath = config.getMockConfigFile().orElseThrow();

        StepFunctionsConfig copy = config.toBuilder().allowPlaintextHttp(false).build();

        assertThat(copy.getMockConfigFile()).contains(originalPath);
        assertThat(copy.getMockConfig()).contains(MOCK_CONFIG_JSON);

        GenericContainer<?> container = genericContainer();
        copy.applyFileMountsToContainer(container);
        assertThat(pendingCopies(container).values()).containsExactly(originalPath);
    }

    @Test
    void shouldRegenerateMockConfigFileWhenContentIsSetAgain() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();
        String originalPath = config.getMockConfigFile().orElseThrow();

        StepFunctionsConfig updated = config.toBuilder().mockConfig("{}").build();

        assertThat(updated.getMockConfigFile()).isPresent();
        assertThat(updated.getMockConfigFile().orElseThrow()).isNotEqualTo(originalPath);
    }

    @Test
    void shouldPreserveMockConfigPathOnToBuilder() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfigFile("/etc/floci/MockConfigFile.json")
                .build();
        StepFunctionsConfig copy = config.toBuilder().build();
        assertThat(copy.getMockConfigFile()).contains("/etc/floci/MockConfigFile.json");
    }

    @Test
    void shouldNotMountAnyFileForDefaultConfig() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder().build().applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldNotMountFileForExplicitMockConfigFilePath() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder()
                .mockConfigFile("/etc/floci/MockConfigFile.json")
                .build()
                .applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldCopyMockConfigContentIntoContainer() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();
        String containerPath = config.getMockConfigFile().orElseThrow();

        GenericContainer<?> container = genericContainer();
        config.applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).hasSize(1);
        assertThat(contentCopiedTo(container, containerPath)).contains(MOCK_CONFIG_JSON);
    }

    @Test
    void shouldNotCopyMockConfigContentWhenServiceDisabled() {
        GenericContainer<?> container = genericContainer();
        StepFunctionsConfig.builder()
                .enabled(false)
                .mockConfig(MOCK_CONFIG_JSON)
                .build()
                .applyFileMountsToContainer(container);

        assertThat(pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldKeepMockConfigContentConsistentWhenAppliedRepeatedly() {
        StepFunctionsConfig config = StepFunctionsConfig.builder()
                .mockConfig(MOCK_CONFIG_JSON)
                .build();
        String containerPath = config.getMockConfigFile().orElseThrow();

        GenericContainer<?> container = genericContainer();
        config.applyFileMountsToContainer(container);
        config.applyFileMountsToContainer(container);

        // Re-applying targets the same path with the same content (a harmless repeat copy on start).
        assertThat(pendingCopies(container).values()).containsOnly(containerPath);
        assertThat(contentCopiedTo(container, containerPath)).contains(MOCK_CONFIG_JSON);
    }
}

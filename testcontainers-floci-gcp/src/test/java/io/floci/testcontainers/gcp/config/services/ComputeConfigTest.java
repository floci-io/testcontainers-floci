package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.TransferableCopyInspector;

import java.time.Duration;
import java.util.List;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ComputeConfigTest {

    private static final String IMAGE_CATALOG =
            "{\"images\": [{\"family\": \"golden\", \"prefixes\": [\"golden-\"], \"dockerImage\": \"registry.local/golden:1\"}]}";

    @Test
    void shouldApplyDefaultComputeConfig() {
        ComputeConfig config = ComputeConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getOperationDelayMs()).isEqualTo(50);
        assertThat(config.getRegions()).isEmpty();
        assertThat(config.isMock()).isTrue();
        assertThat(config.getDefaultImage()).isEqualTo("debian:12");
        assertThat(config.getImageCatalogPath()).isEmpty();
        assertThat(config.getImageCatalog()).isEmpty();
        assertThat(config.getDockerNetwork()).isEmpty();
        assertThat(config.isInstanceResourceLimits()).isTrue();
        assertThat(config.isAllowPrivilegedContainers()).isFalse();
        assertThat(config.isReconcileContainersOnStartup()).isTrue();
        assertThat(config.getLaunchTimeout()).isEqualTo(Duration.ofMinutes(5));
        assertThat(config.getStartupScriptTimeout()).isEqualTo(Duration.ofMinutes(30));
        assertThat(config.getShutdownScriptTimeout()).isEqualTo(Duration.ofSeconds(90));
        assertThat(config.isMetadataServerEnabled()).isTrue();
        assertThat(config.getMetadataServerDefaultWaitTimeout()).isEqualTo(Duration.ofSeconds(300));
    }

    @Test
    void shouldApplyCustomComputeConfig() {
        ComputeConfig config = customConfig().enabled(false).build();
        assertThat(config.isEnabled()).isFalse();
        assertCustomValues(config);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", "50")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_REGIONS")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_MOCK", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_DEFAULT_IMAGE", "debian:12")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_IMAGE_CATALOG_PATH")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_DOCKER_NETWORK")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_INSTANCE_RESOURCE_LIMITS", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ALLOW_PRIVILEGED_CONTAINERS", "false")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_RECONCILE_CONTAINERS_ON_STARTUP", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_LAUNCH_TIMEOUT", "300s")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_STARTUP_SCRIPT_TIMEOUT", "1800s")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_SHUTDOWN_SCRIPT_TIMEOUT", "90s")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT", "300s");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        customConfig().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS", "0")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_REGIONS", "europe-west3,us-east1")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_MOCK", "false")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_DEFAULT_IMAGE", "ubuntu:24.04")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_IMAGE_CATALOG_PATH", "/tmp/image-catalog.json")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_DOCKER_NETWORK", "compute-net")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_INSTANCE_RESOURCE_LIMITS", "false")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ALLOW_PRIVILEGED_CONTAINERS", "true")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_RECONCILE_CONTAINERS_ON_STARTUP", "false")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_LAUNCH_TIMEOUT", "60s")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_STARTUP_SCRIPT_TIMEOUT", "120s")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_SHUTDOWN_SCRIPT_TIMEOUT", "1500ms")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_ENABLED", "false")
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT", "10s");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_COMPUTE_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_OPERATION_DELAY_MS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_REGIONS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_MOCK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_DEFAULT_IMAGE")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_IMAGE_CATALOG_PATH")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_DOCKER_NETWORK")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_INSTANCE_RESOURCE_LIMITS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_ALLOW_PRIVILEGED_CONTAINERS")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_RECONCILE_CONTAINERS_ON_STARTUP")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_LAUNCH_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_STARTUP_SCRIPT_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_SHUTDOWN_SCRIPT_TIMEOUT")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_ENABLED")
                .doesNotContainKey("FLOCI_GCP_SERVICES_COMPUTE_METADATA_SERVER_DEFAULT_WAIT_TIMEOUT");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ComputeConfig config = customConfig().enabled(false).build();
        ComputeConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertCustomValues(copy);
    }

    @Test
    void shouldRequireDockerSocketWhileEnabledAndNotMocked() {
        assertThat(ComputeConfig.builder().mock(false).build().requiresDockerSocket()).isTrue();
        assertThat(ComputeConfig.builder().mock(true).build().requiresDockerSocket()).isFalse();
        assertThat(ComputeConfig.builder().enabled(false).mock(false).build().requiresDockerSocket()).isFalse();
    }

    @Test
    void shouldCopyImageCatalogContentIntoContainer() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig config = ComputeConfig.builder().imageCatalog(IMAGE_CATALOG).build();

        config.applyEnvVarsToContainer(container);
        config.applyFileMountsToContainer(container);

        String imageCatalogPath = config.getImageCatalogPath().orElseThrow();
        assertThat(imageCatalogPath).startsWith("/tmp/floci-gcp-compute-image-catalog-").endsWith(".json");
        assertThat(config.getImageCatalog()).contains(IMAGE_CATALOG);
        assertThat(container.getEnvMap()).containsEntry("FLOCI_GCP_SERVICES_COMPUTE_IMAGE_CATALOG_PATH", imageCatalogPath);
        assertThat(TransferableCopyInspector.contentCopiedTo(container, imageCatalogPath)).contains(IMAGE_CATALOG);
    }

    @Test
    void shouldNotCopyAnythingForExplicitImageCatalogPath() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig config = ComputeConfig.builder().imageCatalogPath("/config/image-catalog.json").build();

        config.applyFileMountsToContainer(container);

        assertThat(config.getImageCatalog()).isEmpty();
        assertThat(TransferableCopyInspector.pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldNotCopyImageCatalogWhenDisabled() {
        GenericContainer<?> container = genericContainer();
        ComputeConfig.builder().enabled(false).imageCatalog(IMAGE_CATALOG).build().applyFileMountsToContainer(container);

        assertThat(TransferableCopyInspector.pendingCopies(container)).isEmpty();
    }

    @Test
    void shouldReplaceImageCatalogPathAndContentWithEachOther() {
        ComputeConfig pathAfterContent = ComputeConfig.builder()
                .imageCatalog(IMAGE_CATALOG).imageCatalogPath("/config/image-catalog.json").build();
        ComputeConfig contentAfterPath = ComputeConfig.builder()
                .imageCatalogPath("/config/image-catalog.json").imageCatalog(IMAGE_CATALOG).build();

        assertThat(pathAfterContent.getImageCatalogPath()).contains("/config/image-catalog.json");
        assertThat(pathAfterContent.getImageCatalog()).isEmpty();
        assertThat(contentAfterPath.getImageCatalogPath()).hasValueSatisfying(path -> assertThat(path).startsWith("/tmp/"));
        assertThat(contentAfterPath.getImageCatalog()).contains(IMAGE_CATALOG);
    }

    @Test
    void shouldPreserveImageCatalogContentOnToBuilder() {
        ComputeConfig config = ComputeConfig.builder().imageCatalog(IMAGE_CATALOG).build();

        ComputeConfig copy = config.toBuilder().build();

        assertThat(copy.getImageCatalog()).contains(IMAGE_CATALOG);
        assertThat(copy.getImageCatalogPath()).isEqualTo(config.getImageCatalogPath());
    }

    private static ComputeConfig.Builder customConfig() {
        return ComputeConfig.builder()
                .operationDelayMs(0)
                .regions(List.of("europe-west3", "us-east1"))
                .mock(false)
                .defaultImage("ubuntu:24.04")
                .imageCatalogPath("/tmp/image-catalog.json")
                .dockerNetwork("compute-net")
                .instanceResourceLimits(false)
                .allowPrivilegedContainers(true)
                .reconcileContainersOnStartup(false)
                .launchTimeout(Duration.ofMinutes(1))
                .startupScriptTimeout(Duration.ofMinutes(2))
                .shutdownScriptTimeout(Duration.ofMillis(1500))
                .metadataServerEnabled(false)
                .metadataServerDefaultWaitTimeout(Duration.ofSeconds(10));
    }

    private static void assertCustomValues(ComputeConfig config) {
        assertThat(config.getOperationDelayMs()).isZero();
        assertThat(config.getRegions()).contains(List.of("europe-west3", "us-east1"));
        assertThat(config.isMock()).isFalse();
        assertThat(config.getDefaultImage()).isEqualTo("ubuntu:24.04");
        assertThat(config.getImageCatalogPath()).contains("/tmp/image-catalog.json");
        assertThat(config.getImageCatalog()).isEmpty();
        assertThat(config.getDockerNetwork()).contains("compute-net");
        assertThat(config.isInstanceResourceLimits()).isFalse();
        assertThat(config.isAllowPrivilegedContainers()).isTrue();
        assertThat(config.isReconcileContainersOnStartup()).isFalse();
        assertThat(config.getLaunchTimeout()).isEqualTo(Duration.ofMinutes(1));
        assertThat(config.getStartupScriptTimeout()).isEqualTo(Duration.ofMinutes(2));
        assertThat(config.getShutdownScriptTimeout()).isEqualTo(Duration.ofMillis(1500));
        assertThat(config.isMetadataServerEnabled()).isFalse();
        assertThat(config.getMetadataServerDefaultWaitTimeout()).isEqualTo(Duration.ofSeconds(10));
    }
}

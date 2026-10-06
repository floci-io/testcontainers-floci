package io.floci.testcontainers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectVolumeResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Container;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.utility.TestcontainersConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FlociContainerCleanupTest {

    private static final String IMAGE = "floci/floci:2.1.0";
    private static final String TEST_LABEL = "com.example.cleanup-test";

    @Test
    void shouldCleanOnlyOwnedResourcesOnStop() {
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        try (FlociContainer first = floci(firstId); FlociContainer second = floci(secondId)) {
            first.start();
            second.start();
            createDatabase(first);
            createDatabase(second);

            assertThat(containers(firstId)).hasSize(1);
            assertThat(volumes(firstId)).hasSize(1);
            first.stop();
            assertResourcesRemoved(firstId);
            assertThat(second.isRunning()).isTrue();
            assertThat(containers(secondId)).hasSize(1);
            assertThat(volumes(secondId)).hasSize(1);

            docker().killContainerCmd(second.getContainerId()).exec();
            second.stop();
            assertResourcesRemoved(secondId);
            second.stop();
        } finally {
            removeTestResources(firstId);
            removeTestResources(secondId);
        }
    }

    @Test
    void shouldDeletePersistentStorageWhenResourceListingFails() throws Exception {
        for (String operation : List.of("listContainersCmd", "listVolumesCmd")) {
            try (FailingDockerContainer container = new FailingDockerContainer()) {
                container.disableAllServices().withStorageConfig(config -> config.randomHostPersistentPath());
                Path storage = container.getStorageConfig().getHostPersistentPath().orElseThrow();
                Files.writeString(storage.resolve("retained-data"), "test");
                container.start();
                RuntimeException failure = new IllegalStateException("Docker resource listing failed");
                container.failOperation(operation, failure);

                assertThatThrownBy(container::stop).isSameAs(failure);
                assertThat(storage).doesNotExist();
            }
        }
    }

    @Test
    void shouldReapResourcesWhenTestJvmIsKilled(@TempDir Path directory) throws Exception {
        // Match ResourceReaper's environment-only switch; it does not read a property for disabling Ryuk.
        assumeFalse(Boolean.parseBoolean(System.getenv("TESTCONTAINERS_RYUK_DISABLED")), "Ryuk is disabled");
        String id = UUID.randomUUID().toString();
        Path ready = directory.resolve("ready");
        Path log = directory.resolve("child.log");
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
                CrashingTestProcess.class.getName(), id, ready.toString())
                .redirectErrorStream(true)
                .redirectOutput(log.toFile())
                .start();
        try {
            await().atMost(Duration.ofMinutes(2)).untilAsserted(() -> {
                assertThat(process.isAlive()).withFailMessage("Child JVM exited: %s", Files.readString(log)).isTrue();
                assertThat(ready).exists();
            });
            assertThat(containers(id)).hasSize(1);
            assertThat(volumes(id)).hasSize(1);
            process.destroyForcibly();
            assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue();

            await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> assertResourcesRemoved(id));
        } finally {
            process.destroyForcibly();
            process.waitFor(10, TimeUnit.SECONDS);
            removeTestResources(id);
        }
    }

    @Test
    void shouldReuseUntilExplicitlyStopped() {
        Properties properties = TestcontainersConfiguration.getInstance().getUserProperties();
        Object previous = properties.setProperty("testcontainers.reuse.enable", "true");
        try (FlociContainer first = new FlociContainer(IMAGE).disableAllServices().withReuse(true);
             FlociContainer second = new FlociContainer(IMAGE).disableAllServices().withReuse(true)) {
            assumeTrue(TestcontainersConfiguration.getInstance().environmentSupportsReuse(),
                    "Container reuse is disabled by the environment");
            first.start();
            second.start();
            assertThat(second.getContainerId()).isEqualTo(first.getContainerId());
            assertThat(first.getContainerInfo().getConfig().getEnv())
                    .noneMatch(entry -> entry.startsWith("FLOCI_DOCKER_EXTRA_LABELS_"));

            String containerId = second.getContainerId();
            second.stop();
            assertThatThrownBy(() -> docker().inspectContainerCmd(containerId).exec())
                    .isInstanceOf(NotFoundException.class);
        } finally {
            if (previous == null) {
                properties.remove("testcontainers.reuse.enable");
            } else {
                properties.put("testcontainers.reuse.enable", previous);
            }
        }
    }

    private static FlociContainer floci(String id) {
        return new FlociContainer(IMAGE).disableAllServices()
                .withRdsConfig(config -> config.enabled(true))
                .withEnv("FLOCI_DOCKER_EXTRA_LABELS_0__KEY", TEST_LABEL)
                .withEnv("FLOCI_DOCKER_EXTRA_LABELS_0__VALUE", id);
    }

    private static void createDatabase(FlociContainer floci) {
        try (RdsClient rds = RdsClient.builder()
                .endpointOverride(URI.create(floci.getEndpoint()))
                .region(Region.of(floci.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(floci.getAccessKey(), floci.getSecretKey())))
                .build()) {
            rds.createDBInstance(request -> request.dbInstanceIdentifier("cleanup-test")
                    .engine("postgres").engineVersion("18.4")
                    .dbInstanceClass("db.t3.micro").masterUsername("admin").masterUserPassword("password123"));
            rds.waiter().waitUntilDBInstanceAvailable(request -> request.dbInstanceIdentifier("cleanup-test"),
                    config -> config.waitTimeout(Duration.ofSeconds(60)))
                    .matched().response().orElseThrow();
        }
    }

    private static void assertResourcesRemoved(String id) {
        assertThat(containers(id)).isEmpty();
        assertThat(volumes(id)).isEmpty();
    }

    private static DockerClient docker() {
        return DockerClientFactory.instance().client();
    }

    private static List<Container> containers(String id) {
        return docker().listContainersCmd().withShowAll(true).withLabelFilter(Map.of(TEST_LABEL, id)).exec();
    }

    private static List<InspectVolumeResponse> volumes(String id) {
        return docker().listVolumesCmd().withFilter("label", List.of(TEST_LABEL + "=" + id)).exec().getVolumes();
    }

    private static void removeTestResources(String id) {
        for (Container container : containers(id)) {
            docker().removeContainerCmd(container.getId()).withForce(true).withRemoveVolumes(true).exec();
        }
        for (InspectVolumeResponse volume : volumes(id)) {
            docker().removeVolumeCmd(volume.getName()).exec();
        }
    }

    private static class FailingDockerContainer extends FlociContainer {
        private FailingDockerContainer() {
            super(IMAGE);
        }

        private void failOperation(String operation, RuntimeException failure) {
            DockerClient delegate = dockerClient;
            dockerClient = (DockerClient) Proxy.newProxyInstance(DockerClient.class.getClassLoader(),
                    new Class<?>[] {DockerClient.class}, (proxy, method, arguments) -> {
                        if (method.getName().equals(operation)) {
                            throw failure;
                        }
                        try {
                            return method.invoke(delegate, arguments);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    });
        }
    }

    public static class CrashingTestProcess {
        public static void main(String[] args) throws Exception {
            FlociContainer floci = floci(args[0]);
            floci.start();
            createDatabase(floci);
            Files.writeString(Path.of(args[1]), "ready");
            Thread.sleep(Long.MAX_VALUE);
        }
    }
}

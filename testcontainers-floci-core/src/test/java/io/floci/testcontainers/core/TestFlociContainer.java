package io.floci.testcontainers.core;

import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.util.function.Consumer;

/**
 * Minimal {@link AbstractFlociContainer} subclass with two dummy services, used to test the provider-independent
 * container logic without depending on any real Floci emulator.
 */
class TestFlociContainer extends AbstractFlociContainer<TestFlociContainer> {

    static final DockerImageName IMAGE_NAME = DockerImageName.parse("floci/floci-test");
    static final int PORT = 4500;
    static final String LOG_LEVEL_ENV_VAR = "QUARKUS_LOG_CATEGORY__IO_FLOCI_TEST__LEVEL";
    static final String DOCKER_NETWORK_ENV_VAR = "FLOCI_TEST_SERVICES_DOCKER_NETWORK";
    static final String RESOURCE_NAMESPACE_ENV_VAR = "FLOCI_TEST_DOCKER_RESOURCE_NAMESPACE";
    static final String HEALTH_PATH = "/_floci-test/health";

    static final CloudDescriptor DESCRIPTOR = CloudDescriptor.builder()
            .name("test")
            .image("floci/floci-test")
            .port(PORT)
            .envPrefix("FLOCI_TEST_")
            .healthPath(HEALTH_PATH)
            .resetPath("/_floci-test/state/reset")
            .logLevelEnvVar(LOG_LEVEL_ENV_VAR)
            .networkEnvVar(DOCKER_NETWORK_ENV_VAR)
            .resourceNamespaceEnvVar(RESOURCE_NAMESPACE_ENV_VAR)
            .tlsHttpsPortEnvVar("FLOCI_TEST_TLS_HTTPS_PORT")
            .defaultEnv("FLOCI_TEST_TLS_HTTPS_PORT", "0")
            .build();

    private final ServiceConfigRef<SimpleServiceConfig> simpleConfig = registerServiceConfig(SimpleServiceConfig.builder().build());
    private final ServiceConfigRef<DockerBackedServiceConfig> dockerBackedConfig = registerServiceConfig(DockerBackedServiceConfig.builder().build());

    private String globalSetting = "default";

    TestFlociContainer() {
        this(IMAGE_NAME.withTag("latest"));
    }

    TestFlociContainer(DockerImageName dockerImageName) {
        super(dockerImageName, DESCRIPTOR);
        applyAllConfigs();
    }

    @Override
    protected void applyGlobalEnvVars() {
        withEnv("FLOCI_TEST_GLOBAL_SETTING", globalSetting);
    }

    TestFlociContainer withGlobalSetting(String globalSetting) {
        this.globalSetting = globalSetting;
        applyGlobalEnvVars();
        return this;
    }

    WaitStrategy waitStrategy() {
        return getWaitStrategy();
    }

    SimpleServiceConfig getSimpleConfig() {
        return simpleConfig.get();
    }

    TestFlociContainer withSimpleConfig(Consumer<SimpleServiceConfig.Builder> configurer) {
        return updateServiceConfig(simpleConfig, configurer);
    }

    DockerBackedServiceConfig getDockerBackedConfig() {
        return dockerBackedConfig.get();
    }

    TestFlociContainer withDockerBackedConfig(Consumer<DockerBackedServiceConfig.Builder> configurer) {
        return updateServiceConfig(dockerBackedConfig, configurer);
    }
}

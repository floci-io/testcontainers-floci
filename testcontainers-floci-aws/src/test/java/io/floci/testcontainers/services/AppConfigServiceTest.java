package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfig.model.Application;
import software.amazon.awssdk.services.appconfig.model.ConfigurationProfileSummary;
import software.amazon.awssdk.services.appconfig.model.Environment;
import software.amazon.awssdk.services.appconfig.model.EnvironmentState;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AppConfigServiceTest extends AbstractServiceTest {

    static AppConfigClient appConfig;

    static String applicationId;
    static String environmentId;
    static String configProfileId;

    private static final String APP_NAME = "test-app-" + System.currentTimeMillis();
    private static final String ENV_NAME = "test-env-" + System.currentTimeMillis();
    private static final String PROFILE_NAME = "test-profile-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        appConfig = client(AppConfigClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateApplication() {
        applicationId = appConfig.createApplication(b -> b.name(APP_NAME)).id();

        assertThat(applicationId).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldListApplications() {
        List<Application> apps = appConfig.listApplications(b -> {}).items();

        assertThat(apps).anyMatch(a -> a.name().equals(APP_NAME));
    }

    @Test
    @Order(3)
    void shouldCreateEnvironment() {
        environmentId = appConfig.createEnvironment(b -> b
                .applicationId(applicationId)
                .name(ENV_NAME))
                .id();

        assertThat(environmentId).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldListEnvironments() {
        List<Environment> envs = appConfig.listEnvironments(b -> b.applicationId(applicationId)).items();

        assertThat(envs).anyMatch(e -> e.name().equals(ENV_NAME));
    }

    @Test
    @Order(5)
    void shouldCreateConfigurationProfile() {
        configProfileId = appConfig.createConfigurationProfile(b -> b
                .applicationId(applicationId)
                .name(PROFILE_NAME)
                .locationUri("hosted"))
                .id();

        assertThat(configProfileId).isNotBlank();
    }

    @Test
    @Order(6)
    void shouldListConfigurationProfiles() {
        List<ConfigurationProfileSummary> profiles = appConfig
                .listConfigurationProfiles(b -> b.applicationId(applicationId))
                .items();

        assertThat(profiles).anyMatch(p -> p.name().equals(PROFILE_NAME));
    }

    @Test
    @Order(7)
    void shouldDeleteApplication() {
        appConfig.deleteApplication(b -> b.applicationId(applicationId));

        List<Application> apps = appConfig.listApplications(b -> {}).items();
        assertThat(apps).noneMatch(a -> a.id().equals(applicationId));
    }
}

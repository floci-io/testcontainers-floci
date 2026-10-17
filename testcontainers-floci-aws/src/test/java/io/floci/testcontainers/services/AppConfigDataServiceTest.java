package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.appconfig.AppConfigClient;
import software.amazon.awssdk.services.appconfigdata.AppConfigDataClient;
import software.amazon.awssdk.services.appconfigdata.model.GetLatestConfigurationResponse;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AppConfigDataServiceTest extends AbstractServiceTest {

    static AppConfigClient appConfig;
    static AppConfigDataClient appConfigData;

    static String applicationId;
    static String environmentId;
    static String configProfileId;
    static String initialToken;

    private static final String APP_NAME = "test-data-app-" + System.currentTimeMillis();
    private static final String ENV_NAME = "test-data-env-" + System.currentTimeMillis();
    private static final String PROFILE_NAME = "test-data-profile-" + System.currentTimeMillis();
    private static final String CONFIG_CONTENT = "{\"feature\":\"enabled\"}";

    @BeforeAll
    static void setUp() {
        appConfig = client(AppConfigClient.builder());
        appConfigData = client(AppConfigDataClient.builder());
    }

    @Test
    @Order(1)
    void shouldSetUpAppConfigResources() {
        applicationId = appConfig.createApplication(b -> b.name(APP_NAME)).id();
        environmentId = appConfig.createEnvironment(b -> b
                .applicationId(applicationId)
                .name(ENV_NAME))
                .id();
        configProfileId = appConfig.createConfigurationProfile(b -> b
                .applicationId(applicationId)
                .name(PROFILE_NAME)
                .locationUri("hosted"))
                .id();

        appConfig.createHostedConfigurationVersion(b -> b
                .applicationId(applicationId)
                .configurationProfileId(configProfileId)
                .content(SdkBytes.fromUtf8String(CONFIG_CONTENT))
                .contentType("application/json"));

        assertThat(applicationId).isNotBlank();
        assertThat(environmentId).isNotBlank();
        assertThat(configProfileId).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldStartConfigurationSession() {
        initialToken = appConfigData.startConfigurationSession(b -> b
                .applicationIdentifier(applicationId)
                .environmentIdentifier(environmentId)
                .configurationProfileIdentifier(configProfileId))
                .initialConfigurationToken();

        assertThat(initialToken).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldGetLatestConfiguration() {
        GetLatestConfigurationResponse response = appConfigData.getLatestConfiguration(b -> b
                .configurationToken(initialToken));

        assertThat(response.nextPollConfigurationToken()).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldCleanUp() {
        appConfig.deleteApplication(b -> b.applicationId(applicationId));
    }
}

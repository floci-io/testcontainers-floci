package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.mwaa.MwaaClient;
import software.amazon.awssdk.services.mwaa.model.EnvironmentStatus;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
@Disabled("Too time consuming and flaky")
class MwaaServiceTest extends AbstractServiceTest {

    static MwaaClient mwaa;

    private static final String SOURCE_BUCKET = "mwaa-bucket";
    private static final String ENVIRONMENT_NAME = "test-environment-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        mwaa = client(MwaaClient.builder());

        // Real (non-mock) mode doesn't validate the bucket up front, but it does poll
        // DagS3Path from it once the environment is up - create the bucket for realism.
        S3Client s3 = client(S3Client.builder().forcePathStyle(true));
        s3.createBucket(b -> b.bucket(SOURCE_BUCKET));
    }

    @Test
    @Order(1)
    void shouldCreateEnvironment() {
        var response = mwaa.createEnvironment(b -> b
                .name(ENVIRONMENT_NAME)
                .executionRoleArn("arn:aws:iam::000000000000:role/mwaa-role")
                .sourceBucketArn("arn:aws:s3:::" + SOURCE_BUCKET)
                .dagS3Path("dags"));

        assertThat(response.arn()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetEnvironment() {
        // Real mode boots dedicated Postgres + Airflow containers and only reaches AVAILABLE
        // once Airflow's own /health endpoint reports the metadatabase and scheduler healthy,
        // so this happens asynchronously rather than immediately.
        await().atMost(Duration.ofSeconds(300))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
                    assertThat(response.environment().status()).isEqualTo(EnvironmentStatus.AVAILABLE);
                });

        var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
        assertThat(response.environment().name()).isEqualTo(ENVIRONMENT_NAME);
        assertThat(response.environment().airflowVersion()).isEqualTo("2.10.5");
    }

    @Test
    @Order(3)
    void shouldListEnvironmentsContainsCreatedEnvironment() {
        List<String> environments = mwaa.listEnvironments(b -> {}).environments();

        assertThat(environments).contains(ENVIRONMENT_NAME);
    }

    @Test
    @Order(4)
    void shouldUpdateEnvironment() {
        String newRoleArn = "arn:aws:iam::000000000000:role/mwaa-role-updated";

        mwaa.updateEnvironment(b -> b
                .name(ENVIRONMENT_NAME)
                .executionRoleArn(newRoleArn));

        var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));
        assertThat(response.environment().executionRoleArn()).isEqualTo(newRoleArn);
    }

    @Test
    @Order(5)
    void shouldCreateWebLoginToken() {
        var response = mwaa.createWebLoginToken(b -> b.name(ENVIRONMENT_NAME));

        assertThat(response.webToken()).isNotBlank();
        assertThat(response.webServerHostname()).isNotBlank();
    }

    @Test
    @Order(6)
    void shouldCreateCliToken() {
        var response = mwaa.createCliToken(b -> b.name(ENVIRONMENT_NAME));

        assertThat(response.cliToken()).isNotBlank();
        assertThat(response.webServerHostname()).isNotBlank();
    }

    @Test
    @Order(7)
    void shouldDeleteEnvironment() {
        mwaa.deleteEnvironment(b -> b.name(ENVIRONMENT_NAME));

        List<String> environments = mwaa.listEnvironments(b -> {}).environments();
        assertThat(environments).doesNotContain(ENVIRONMENT_NAME);
    }
}

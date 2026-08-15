package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.mwaa.MwaaClient;
import software.amazon.awssdk.services.mwaa.model.EnvironmentStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class MwaaServiceTest extends AbstractServiceTest {

    static MwaaClient mwaa;

    private static final String ENVIRONMENT_NAME = "test-environment-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        mwaa = client(MwaaClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateEnvironment() {
        var response = mwaa.createEnvironment(b -> b
                .name(ENVIRONMENT_NAME)
                .executionRoleArn("arn:aws:iam::000000000000:role/mwaa-role")
                .sourceBucketArn("arn:aws:s3:::mwaa-bucket")
                .dagS3Path("dags"));

        assertThat(response.arn()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetEnvironment() {
        var response = mwaa.getEnvironment(b -> b.name(ENVIRONMENT_NAME));

        assertThat(response.environment().name()).isEqualTo(ENVIRONMENT_NAME);
        assertThat(response.environment().status()).isEqualTo(EnvironmentStatus.AVAILABLE);
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

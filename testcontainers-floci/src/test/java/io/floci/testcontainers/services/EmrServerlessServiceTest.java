package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.emrserverless.EmrServerlessClient;
import software.amazon.awssdk.services.emrserverless.model.ApplicationSummary;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class EmrServerlessServiceTest extends AbstractServiceTest {

    static EmrServerlessClient emrServerless;

    static String applicationId;

    @BeforeAll
    static void setUp() {
        emrServerless = client(EmrServerlessClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateApplication() {
        var response = emrServerless.createApplication(b -> b
                .name("test-application-" + System.currentTimeMillis())
                .releaseLabel("emr-7.5.0")
                .type("SPARK"));

        assertThat(response.applicationId()).isNotBlank();
        applicationId = response.applicationId();
    }

    @Test
    @Order(2)
    void shouldGetApplication() {
        var response = emrServerless.getApplication(b -> b.applicationId(applicationId));

        assertThat(response.application().applicationId()).isEqualTo(applicationId);
        assertThat(response.application().type()).isEqualTo("SPARK");
    }

    @Test
    @Order(3)
    void shouldListApplicationsContainsCreatedApplication() {
        var response = emrServerless.listApplications(b -> {});

        assertThat(response.applications())
                .extracting(ApplicationSummary::id)
                .contains(applicationId);
    }

    @Test
    @Order(4)
    void shouldStartApplication() {
        emrServerless.startApplication(b -> b.applicationId(applicationId));

        var response = emrServerless.getApplication(b -> b.applicationId(applicationId));
        assertThat(response.application().applicationId()).isEqualTo(applicationId);
    }

    @Test
    @Order(5)
    void shouldStopApplication() {
        emrServerless.stopApplication(b -> b.applicationId(applicationId));

        var response = emrServerless.getApplication(b -> b.applicationId(applicationId));
        assertThat(response.application().applicationId()).isEqualTo(applicationId);
    }

    @Test
    @Order(6)
    void shouldDeleteApplication() {
        emrServerless.deleteApplication(b -> b.applicationId(applicationId));

        var response = emrServerless.listApplications(b -> {});
        assertThat(response.applications())
                .extracting(ApplicationSummary::id)
                .doesNotContain(applicationId);
    }
}

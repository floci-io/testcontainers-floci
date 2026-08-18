package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import software.amazon.awssdk.services.guardduty.model.DetectorStatus;
import software.amazon.awssdk.services.guardduty.model.FindingPublishingFrequency;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class GuardDutyServiceTest extends AbstractServiceTest {

    static GuardDutyClient guardDuty;

    static String detectorId;

    @BeforeAll
    static void setUp() {
        guardDuty = client(GuardDutyClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateDetector() {
        var response = guardDuty.createDetector(b -> b
                .enable(true)
                .findingPublishingFrequency(FindingPublishingFrequency.SIX_HOURS));

        assertThat(response.detectorId()).isNotBlank();
        detectorId = response.detectorId();
    }

    @Test
    @Order(2)
    void shouldGetDetector() {
        var response = guardDuty.getDetector(b -> b.detectorId(detectorId));

        assertThat(response.status()).isEqualTo(DetectorStatus.ENABLED);
        assertThat(response.findingPublishingFrequency()).isEqualTo(FindingPublishingFrequency.SIX_HOURS);
    }

    @Test
    @Order(3)
    void shouldListDetectorsContainsCreatedDetector() {
        var response = guardDuty.listDetectors(b -> {});

        assertThat(response.detectorIds()).contains(detectorId);
    }

    @Test
    @Order(4)
    void shouldUpdateDetector() {
        guardDuty.updateDetector(b -> b.detectorId(detectorId).enable(false));

        var response = guardDuty.getDetector(b -> b.detectorId(detectorId));
        assertThat(response.status()).isEqualTo(DetectorStatus.DISABLED);
    }

    @Test
    @Order(5)
    void shouldDeleteDetector() {
        guardDuty.deleteDetector(b -> b.detectorId(detectorId));

        var response = guardDuty.listDetectors(b -> {});
        assertThat(response.detectorIds()).doesNotContain(detectorId);
    }
}

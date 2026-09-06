package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.controltower.ControlTowerClient;
import software.amazon.awssdk.services.controltower.model.LandingZoneSummary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Floci no longer seeds a Control Tower landing zone; {@code ListLandingZones} stays empty until one
 * is created. The tests are ordered: {@link #shouldCreateLandingZone()} provisions the landing zone
 * the later tests read.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ControlTowerServiceTest extends AbstractServiceTest {

    static ControlTowerClient controlTower;

    // Captured by shouldCreateLandingZone() and reused by the later, ordered tests.
    static String landingZoneArn;

    @BeforeAll
    static void setUp() {
        controlTower = client(ControlTowerClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateLandingZone() {
        Document manifest = Document.mapBuilder()
                .putDocument("governedRegions", Document.listBuilder().addString(floci.getRegion()).build())
                .putDocument("securityRoles", Document.mapBuilder().putBoolean("enabled", true).build())
                .putDocument("accessManagement", Document.mapBuilder().putBoolean("enabled", true).build())
                .build();

        var response = controlTower.createLandingZone(b -> b.version("4.0").manifest(manifest));

        assertThat(response.arn()).contains(":landingzone/");
        assertThat(response.operationIdentifier()).isNotBlank();

        landingZoneArn = response.arn();
    }

    @Test
    @Order(2)
    void shouldListCreatedLandingZone() {
        var response = controlTower.listLandingZones(b -> {});

        assertThat(response.landingZones())
                .extracting(LandingZoneSummary::arn)
                .containsExactly(landingZoneArn);
    }

    @Test
    @Order(3)
    void shouldGetCreatedLandingZone() {
        var response = controlTower.getLandingZone(b -> b.landingZoneIdentifier(landingZoneArn));

        assertThat(response.landingZone().arn()).isEqualTo(landingZoneArn);
        assertThat(response.landingZone().status()).isNotNull();
    }

    @Test
    @Order(4)
    void shouldDeleteLandingZone() {
        var response = controlTower.deleteLandingZone(b -> b.landingZoneIdentifier(landingZoneArn));

        assertThat(response.operationIdentifier()).isNotBlank();
        assertThat(controlTower.listLandingZones(b -> {}).landingZones()).isEmpty();
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.controltower.ControlTowerClient;

import static org.assertj.core.api.Assertions.assertThat;

class ControlTowerServiceTest extends AbstractServiceTest {

    static ControlTowerClient controlTower;

    @BeforeAll
    static void setUp() {
        controlTower = client(ControlTowerClient.builder());
    }

    @Test
    void shouldListSeededLandingZone() {
        var response = controlTower.listLandingZones(b -> {});

        assertThat(response.landingZones()).hasSize(1);
        assertThat(response.landingZones().get(0).arn()).contains(":landingzone/");
    }

    @Test
    void shouldGetSeededLandingZone() {
        String arn = controlTower.listLandingZones(b -> {}).landingZones().get(0).arn();

        var response = controlTower.getLandingZone(b -> b.landingZoneIdentifier(arn));

        assertThat(response.landingZone().arn()).isEqualTo(arn);
        assertThat(response.landingZone().status()).isNotNull();
    }
}

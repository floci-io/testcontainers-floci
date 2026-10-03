package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.controlcatalog.ControlCatalogClient;
import software.amazon.awssdk.services.controlcatalog.model.ControlSummary;

import static org.assertj.core.api.Assertions.assertThat;

class ControlCatalogServiceTest extends AbstractServiceTest {

    private static final String CONTROL_ARN = "arn:aws:controlcatalog:::control/7mo7a2h2ebsq71l8k6uzr96ou";

    static ControlCatalogClient controlCatalog;

    @BeforeAll
    static void setUp() {
        controlCatalog = client(ControlCatalogClient.builder());
    }

    @Test
    void shouldGetControl() {
        var control = controlCatalog.getControl(b -> b.controlArn(CONTROL_ARN));

        assertThat(control.arn()).isEqualTo(CONTROL_ARN);
        assertThat(control.implementation().identifier()).isEqualTo("CT.S3.PV.5");
    }

    @Test
    void shouldListControls() {
        assertThat(controlCatalog.listControls(b -> b.maxResults(100)).controls())
                .isNotEmpty()
                .extracting(ControlSummary::arn)
                .allSatisfy(arn -> assertThat(arn).startsWith("arn:aws:controlcatalog:::control/"));
    }
}

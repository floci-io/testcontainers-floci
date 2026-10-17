package io.floci.testcontainers.gcp.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static io.floci.testcontainers.core.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class ServiceUsageConfigTest {

    private static final String DEFAULT_ENABLED = "analyticshub.googleapis.com,apptopology.googleapis.com,"
            + "bigquery.googleapis.com,bigqueryconnection.googleapis.com,bigquerydatapolicy.googleapis.com,"
            + "bigquerydatatransfer.googleapis.com,bigquerymigration.googleapis.com,"
            + "bigqueryreservation.googleapis.com,bigquerystorage.googleapis.com,cloudapis.googleapis.com,"
            + "cloudtrace.googleapis.com,dataform.googleapis.com,dataplex.googleapis.com,"
            + "datastore.googleapis.com,logging.googleapis.com,monitoring.googleapis.com,"
            + "servicemanagement.googleapis.com,serviceusage.googleapis.com,sql-component.googleapis.com,"
            + "storage-api.googleapis.com,storage-component.googleapis.com,storage.googleapis.com,"
            + "telemetry.googleapis.com";

    @Test
    void shouldApplyDefaultServiceUsageConfig() {
        ServiceUsageConfig config = ServiceUsageConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.isEnforcementEnabled()).isFalse();
        assertThat(config.getDefaultEnabled()).containsExactly(DEFAULT_ENABLED.split(","));
    }

    @Test
    void shouldApplyCustomServiceUsageConfig() {
        ServiceUsageConfig config = ServiceUsageConfig.builder()
                .enabled(false)
                .enforcementEnabled(true)
                .defaultEnabled(List.of("storage.googleapis.com", "pubsub.googleapis.com"))
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.isEnforcementEnabled()).isTrue();
        assertThat(config.getDefaultEnabled()).containsExactly("storage.googleapis.com", "pubsub.googleapis.com");
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENFORCEMENT_ENABLED", "false")
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_DEFAULT_ENABLED", DEFAULT_ENABLED);
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder()
                .enforcementEnabled(true)
                .defaultEnabled(List.of("storage.googleapis.com", "pubsub.googleapis.com"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENFORCEMENT_ENABLED", "true")
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_DEFAULT_ENABLED",
                        "storage.googleapis.com,pubsub.googleapis.com");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        ServiceUsageConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENABLED", "false")
                .doesNotContainKey("FLOCI_GCP_SERVICES_SERVICEUSAGE_ENFORCEMENT_ENABLED")
                .doesNotContainKey("FLOCI_GCP_SERVICES_SERVICEUSAGE_DEFAULT_ENABLED");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        ServiceUsageConfig config = ServiceUsageConfig.builder()
                .enabled(false)
                .enforcementEnabled(true)
                .defaultEnabled(List.of("pubsub.googleapis.com"))
                .build();
        ServiceUsageConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.isEnforcementEnabled()).isTrue();
        assertThat(copy.getDefaultEnabled()).containsExactly("pubsub.googleapis.com");
    }
}

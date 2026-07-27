package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class RdsDataConfigTest {

    @Test
    void shouldApplyDefaultRdsDataConfig() {
        RdsDataConfig config = RdsDataConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getTransactionTtlSeconds()).isEqualTo(180);
    }

    @Test
    void shouldApplyCustomRdsDataConfig() {
        RdsDataConfig config = RdsDataConfig.builder()
                .enabled(false)
                .transactionTtlSeconds(300)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getTransactionTtlSeconds()).isEqualTo(300);
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", "180");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder()
                .transactionTtlSeconds(600)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", "600");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RdsDataConfig config = RdsDataConfig.builder()
                .enabled(false)
                .transactionTtlSeconds(60L)
                .build();
        RdsDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getTransactionTtlSeconds()).isEqualTo(60L);
    }

}

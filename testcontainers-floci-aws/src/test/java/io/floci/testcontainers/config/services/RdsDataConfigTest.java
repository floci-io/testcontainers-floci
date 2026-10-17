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
        assertThat(config.isDatabaseResumingExceptionEnabled()).isFalse();
    }

    @Test
    void shouldApplyCustomRdsDataConfig() {
        RdsDataConfig config = RdsDataConfig.builder()
                .enabled(false)
                .transactionTtlSeconds(300)
                .databaseResumingExceptionEnabled(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getTransactionTtlSeconds()).isEqualTo(300);
        assertThat(config.isDatabaseResumingExceptionEnabled()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", "180")
                .containsEntry("FLOCI_SERVICES_RDS_DATA_DATABASE_RESUMING_EXCEPTION_ENABLED", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder()
                .transactionTtlSeconds(600)
                .databaseResumingExceptionEnabled(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS", "600")
                .containsEntry("FLOCI_SERVICES_RDS_DATA_DATABASE_RESUMING_EXCEPTION_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        RdsDataConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_RDS_DATA_ENABLED", "false")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DATA_TRANSACTION_TTL_SECONDS")
                .doesNotContainKey("FLOCI_SERVICES_RDS_DATA_DATABASE_RESUMING_EXCEPTION_ENABLED");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        RdsDataConfig config = RdsDataConfig.builder()
                .enabled(false)
                .transactionTtlSeconds(60L)
                .databaseResumingExceptionEnabled(true)
                .build();
        RdsDataConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getTransactionTtlSeconds()).isEqualTo(60L);
        assertThat(copy.isDatabaseResumingExceptionEnabled()).isTrue();
    }

}

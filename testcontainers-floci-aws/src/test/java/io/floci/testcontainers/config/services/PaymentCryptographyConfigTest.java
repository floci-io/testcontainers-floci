package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentCryptographyConfigTest {

    @Test
    void shouldApplyDefaultPaymentCryptographyConfig() {
        PaymentCryptographyConfig config = PaymentCryptographyConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomPaymentCryptographyConfig() {
        PaymentCryptographyConfig config = PaymentCryptographyConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        PaymentCryptographyConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PAYMENTCRYPTOGRAPHY_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        PaymentCryptographyConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_PAYMENTCRYPTOGRAPHY_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        PaymentCryptographyConfig config = PaymentCryptographyConfig.builder()
                .enabled(false)
                .build();
        PaymentCryptographyConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }

}

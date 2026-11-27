package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.paymentcryptography.PaymentCryptographyClient;
import software.amazon.awssdk.services.paymentcryptography.model.Key;
import software.amazon.awssdk.services.paymentcryptography.model.KeyAlgorithm;
import software.amazon.awssdk.services.paymentcryptography.model.KeyClass;
import software.amazon.awssdk.services.paymentcryptography.model.KeyState;
import software.amazon.awssdk.services.paymentcryptography.model.KeySummary;
import software.amazon.awssdk.services.paymentcryptography.model.KeyUsage;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class PaymentCryptographyServiceTest extends AbstractServiceTest {

    static PaymentCryptographyClient paymentCryptography;

    static String keyArn;
    static String aliasName;

    @BeforeAll
    static void setUp() {
        paymentCryptography = client(PaymentCryptographyClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateKey() {
        Key key = paymentCryptography.createKey(b -> b
                        .keyAttributes(a -> a
                                .keyAlgorithm(KeyAlgorithm.AES_256)
                                .keyClass(KeyClass.SYMMETRIC_KEY)
                                .keyUsage(KeyUsage.TR31_D0_SYMMETRIC_DATA_ENCRYPTION_KEY)
                                .keyModesOfUse(m -> m.encrypt(true).decrypt(true)))
                        .exportable(true))
                .key();
        keyArn = key.keyArn();

        assertThat(keyArn).isNotBlank();
        assertThat(key.keyState()).isEqualTo(KeyState.CREATE_COMPLETE);
        assertThat(key.enabled()).isTrue();
        assertThat(key.keyCheckValue()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetAndListKey() {
        Key key = paymentCryptography.getKey(b -> b.keyIdentifier(keyArn)).key();

        assertThat(key.keyAttributes().keyAlgorithm()).isEqualTo(KeyAlgorithm.AES_256);
        assertThat(key.keyAttributes().keyModesOfUse().encrypt()).isTrue();
        assertThat(paymentCryptography.listKeys(b -> { }).keys())
                .extracting(KeySummary::keyArn)
                .contains(keyArn);
    }

    @Test
    @Order(3)
    void shouldCreateAndGetAlias() {
        aliasName = "alias/testcontainers-" + System.currentTimeMillis();
        paymentCryptography.createAlias(b -> b.aliasName(aliasName).keyArn(keyArn));

        assertThat(paymentCryptography.getAlias(b -> b.aliasName(aliasName)).alias().keyArn()).isEqualTo(keyArn);
    }

    @Test
    @Order(4)
    void shouldDeleteAliasAndKey() {
        paymentCryptography.deleteAlias(b -> b.aliasName(aliasName));
        Key deleted = paymentCryptography.deleteKey(b -> b.keyIdentifier(keyArn)).key();

        assertThat(deleted.keyArn()).isEqualTo(keyArn);
        assertThat(deleted.keyState()).isEqualTo(KeyState.DELETE_PENDING);
    }
}

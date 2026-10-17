package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.lightsail.LightsailClient;
import software.amazon.awssdk.services.lightsail.model.KeyPair;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class LightsailServiceTest extends AbstractServiceTest {

    static LightsailClient lightsail;

    private static final String KEY_PAIR_NAME = "test-key-pair-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        lightsail = client(LightsailClient.builder());
    }

    @Test
    @Order(1)
    void shouldGetKeyPairs() {
        List<KeyPair> keyPairs = lightsail.getKeyPairs().keyPairs();

        assertThat(keyPairs).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateKeyPair() {
        var response = lightsail.createKeyPair(b -> b.keyPairName(KEY_PAIR_NAME));

        assertThat(response.keyPair().name()).isEqualTo(KEY_PAIR_NAME);
        assertThat(response.publicKeyBase64()).isNotBlank();
        assertThat(response.privateKeyBase64()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldGetKeyPair() {
        var response = lightsail.getKeyPair(b -> b.keyPairName(KEY_PAIR_NAME));

        assertThat(response.keyPair().name()).isEqualTo(KEY_PAIR_NAME);
    }

    @Test
    @Order(4)
    void shouldGetKeyPairsContainsCreatedKeyPair() {
        List<KeyPair> keyPairs = lightsail.getKeyPairs().keyPairs();

        assertThat(keyPairs).anyMatch(k -> k.name().equals(KEY_PAIR_NAME));
    }

    @Test
    @Order(5)
    void shouldDeleteKeyPair() {
        lightsail.deleteKeyPair(b -> b.keyPairName(KEY_PAIR_NAME));

        List<KeyPair> keyPairs = lightsail.getKeyPairs().keyPairs();
        assertThat(keyPairs).noneMatch(k -> k.name().equals(KEY_PAIR_NAME));
    }
}

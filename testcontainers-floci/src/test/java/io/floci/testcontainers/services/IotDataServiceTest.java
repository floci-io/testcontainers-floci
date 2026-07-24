package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.iotdataplane.IotDataPlaneClient;
import software.amazon.awssdk.services.iotdataplane.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class IotDataServiceTest extends AbstractServiceTest {

    static IotDataPlaneClient iotData;

    private static final String THING_NAME = "test-thing-shadow-" + System.currentTimeMillis();
    private static final String SHADOW_PAYLOAD = "{\"state\":{\"reported\":{\"temperature\":21}}}";

    @BeforeAll
    static void setUp() {
        iotData = client(IotDataPlaneClient.builder());
    }

    @Test
    @Order(1)
    void shouldUpdateThingShadow() {
        var response = iotData.updateThingShadow(b -> b
                .thingName(THING_NAME)
                .payload(SdkBytes.fromUtf8String(SHADOW_PAYLOAD)));

        assertThat(response.payload().asUtf8String()).contains("\"temperature\":21");
    }

    @Test
    @Order(2)
    void shouldGetThingShadow() {
        var response = iotData.getThingShadow(b -> b.thingName(THING_NAME));

        assertThat(response.payload().asUtf8String()).contains("\"temperature\":21");
    }

    @Test
    @Order(3)
    void shouldDeleteThingShadow() {
        iotData.deleteThingShadow(b -> b.thingName(THING_NAME));

        assertThatThrownBy(() -> iotData.getThingShadow(b -> b.thingName(THING_NAME)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

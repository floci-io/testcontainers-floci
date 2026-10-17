package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.ThingAttribute;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class IotServiceTest extends AbstractServiceTest {

    static IotClient iot;

    private static final String THING_NAME = "test-thing-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        iot = client(IotClient.builder());
    }

    @Test
    @Order(1)
    void shouldListThings() {
        List<ThingAttribute> things = iot.listThings().things();

        assertThat(things).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateThing() {
        var response = iot.createThing(b -> b.thingName(THING_NAME));

        assertThat(response.thingName()).isEqualTo(THING_NAME);
        assertThat(response.thingArn()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldDescribeThing() {
        var response = iot.describeThing(b -> b.thingName(THING_NAME));

        assertThat(response.thingName()).isEqualTo(THING_NAME);
    }

    @Test
    @Order(4)
    void shouldListThingsContainsCreatedThing() {
        List<ThingAttribute> things = iot.listThings().things();

        assertThat(things).anyMatch(t -> t.thingName().equals(THING_NAME));
    }

    @Test
    @Order(5)
    void shouldDeleteThing() {
        iot.deleteThing(b -> b.thingName(THING_NAME));

        List<ThingAttribute> things = iot.listThings().things();
        assertThat(things).noneMatch(t -> t.thingName().equals(THING_NAME));
    }
}

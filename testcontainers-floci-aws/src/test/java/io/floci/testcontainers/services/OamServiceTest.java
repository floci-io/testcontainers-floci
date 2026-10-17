package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.oam.OamClient;
import software.amazon.awssdk.services.oam.model.ListSinksItem;
import software.amazon.awssdk.services.oam.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class OamServiceTest extends AbstractServiceTest {

    static OamClient oam;

    static String sinkArn;

    @BeforeAll
    static void setUp() {
        oam = client(OamClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateSink() {
        var response = oam.createSink(b -> b.name("floci-tc-sink"));

        assertThat(response.arn()).isNotBlank();
        assertThat(response.name()).isEqualTo("floci-tc-sink");
        sinkArn = response.arn();
    }

    @Test
    @Order(2)
    void shouldGetAndListSink() {
        assertThat(oam.getSink(b -> b.identifier(sinkArn)).name()).isEqualTo("floci-tc-sink");
        assertThat(oam.listSinks(b -> {}).items())
                .extracting(ListSinksItem::arn)
                .contains(sinkArn);
    }

    @Test
    @Order(3)
    void shouldDeleteSink() {
        oam.deleteSink(b -> b.identifier(sinkArn));

        assertThatThrownBy(() -> oam.getSink(b -> b.identifier(sinkArn)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

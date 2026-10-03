package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.CreateGuardrailResponse;
import software.amazon.awssdk.services.bedrock.model.GetGuardrailResponse;
import software.amazon.awssdk.services.bedrock.model.GuardrailContentFilterType;
import software.amazon.awssdk.services.bedrock.model.GuardrailFilterStrength;
import software.amazon.awssdk.services.bedrock.model.GuardrailStatus;
import software.amazon.awssdk.services.bedrock.model.GuardrailSummary;
import software.amazon.awssdk.services.bedrock.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class BedrockServiceTest extends AbstractServiceTest {

    static BedrockClient bedrock;

    static String guardrailId;

    @BeforeAll
    static void setUp() {
        bedrock = client(BedrockClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateGuardrail() {
        CreateGuardrailResponse response = bedrock.createGuardrail(b -> b
                .name("floci-tc-guardrail")
                .blockedInputMessaging("Input blocked.")
                .blockedOutputsMessaging("Output blocked.")
                .contentPolicyConfig(c -> c.filtersConfig(f -> f
                        .type(GuardrailContentFilterType.HATE)
                        .inputStrength(GuardrailFilterStrength.HIGH)
                        .outputStrength(GuardrailFilterStrength.HIGH))));

        assertThat(response.guardrailId()).isNotBlank();
        assertThat(response.version()).isEqualTo("DRAFT");
        guardrailId = response.guardrailId();
    }

    @Test
    @Order(2)
    void shouldGetGuardrail() {
        GetGuardrailResponse response = bedrock.getGuardrail(b -> b.guardrailIdentifier(guardrailId));

        assertThat(response.name()).isEqualTo("floci-tc-guardrail");
        assertThat(response.status()).isEqualTo(GuardrailStatus.READY);
        assertThat(response.blockedInputMessaging()).isEqualTo("Input blocked.");
    }

    @Test
    @Order(3)
    void shouldCreateGuardrailVersion() {
        var response = bedrock.createGuardrailVersion(b -> b.guardrailIdentifier(guardrailId));

        assertThat(response.version()).isEqualTo("1");
        assertThat(bedrock.getGuardrail(b -> b.guardrailIdentifier(guardrailId).guardrailVersion("1")).version())
                .isEqualTo("1");
    }

    @Test
    @Order(4)
    void shouldListGuardrails() {
        assertThat(bedrock.listGuardrails(b -> {}).guardrails())
                .extracting(GuardrailSummary::id)
                .contains(guardrailId);
    }

    @Test
    @Order(5)
    void shouldDeleteGuardrail() {
        bedrock.deleteGuardrail(b -> b.guardrailIdentifier(guardrailId));

        assertThatThrownBy(() -> bedrock.getGuardrail(b -> b.guardrailIdentifier(guardrailId)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockagentcore.BedrockAgentCoreClient;
import software.amazon.awssdk.services.bedrockagentcore.model.InvokeAgentRuntimeResponse;

import static org.assertj.core.api.Assertions.assertThat;

class BedrockAgentCoreServiceTest extends AbstractServiceTest {

    static BedrockAgentCoreClient bedrockAgentCore;

    @BeforeAll
    static void setUp() {
        bedrockAgentCore = client(BedrockAgentCoreClient.builder());
    }

    @Test
    void shouldInvokeAgentRuntime() {
        ResponseBytes<InvokeAgentRuntimeResponse> response = bedrockAgentCore.invokeAgentRuntimeAsBytes(b -> b
                .agentRuntimeArn("arn:aws:bedrock-agentcore:us-east-1:000000000000:runtime/test-runtime")
                .contentType("application/json")
                .payload(SdkBytes.fromUtf8String("{\"input\":\"Hello from Floci Bedrock AgentCore!\"}")));

        assertThat(response).isNotNull();
        assertThat(response.asUtf8String()).isEqualTo("{\"output\":\"yes\"}");
    }

}

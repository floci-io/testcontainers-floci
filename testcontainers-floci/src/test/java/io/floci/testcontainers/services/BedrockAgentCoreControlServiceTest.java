package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.bedrockagentcorecontrol.BedrockAgentCoreControlClient;
import software.amazon.awssdk.services.bedrockagentcorecontrol.model.AgentRuntime;
import software.amazon.awssdk.services.bedrockagentcorecontrol.model.CreateAgentRuntimeResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BedrockAgentCoreControlServiceTest extends AbstractServiceTest {

    static BedrockAgentCoreControlClient bedrockAgentCoreControl;

    @BeforeAll
    static void setUp() {
        bedrockAgentCoreControl = client(BedrockAgentCoreControlClient.builder());
    }

    @Test
    void shouldListAgentRuntimes() {
        List<AgentRuntime> runtimes = bedrockAgentCoreControl.listAgentRuntimes(b -> {}).agentRuntimes();

        assertThat(runtimes).isNotNull();
    }

    @Test
    void shouldCreateAndGetAgentRuntime() {
        String runtimeName = "test_runtime_" + System.currentTimeMillis();

        CreateAgentRuntimeResponse created = bedrockAgentCoreControl.createAgentRuntime(b -> b
                .agentRuntimeName(runtimeName)
                .agentRuntimeArtifact(a -> a.containerConfiguration(c -> c
                        .containerUri("000000000000.dkr.ecr.us-east-1.amazonaws.com/test-runtime:latest")))
                .roleArn("arn:aws:iam::000000000000:role/test-role"));

        var response = bedrockAgentCoreControl.getAgentRuntime(b -> b.agentRuntimeId(created.agentRuntimeId()));

        assertThat(response.agentRuntimeName()).isEqualTo(runtimeName);
    }

}

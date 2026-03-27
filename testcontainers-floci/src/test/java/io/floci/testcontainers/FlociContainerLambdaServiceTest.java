package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerLambdaServiceTest extends AbstractFlociContainerServiceTest {

    static LambdaClient lambda;

    @BeforeAll
    static void setUp() {
        lambda = client(LambdaClient.builder());
    }

    @Test
    void shouldListFunctions() {
        List<FunctionConfiguration> functions = lambda.listFunctions().functions();

        // A fresh Floci instance has no functions — just verify the API responds
        assertThat(functions).isNotNull();
    }

}

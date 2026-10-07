package io.floci.testcontainers.gcp.services;

import com.google.cloud.functions.v2.BuildConfig;
import com.google.cloud.functions.v2.CreateFunctionRequest;
import com.google.cloud.functions.v2.Function;
import com.google.cloud.functions.v2.FunctionServiceClient;
import com.google.cloud.functions.v2.FunctionServiceSettings;
import com.google.cloud.functions.v2.LocationName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CloudFunctionsServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateFunction() throws Exception {
        String functionId = uniqueName("function");

        try (FunctionServiceClient client = FunctionServiceClient.create(FunctionServiceSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            Function created = client.createFunctionAsync(CreateFunctionRequest.newBuilder()
                            .setParent(LocationName.of(projectId(), "us-central1").toString())
                            .setFunctionId(functionId)
                            .setFunction(Function.newBuilder()
                                    .setBuildConfig(BuildConfig.newBuilder()
                                            .setRuntime("java21")
                                            .setEntryPoint("ExampleFunction")))
                            .build())
                    .get(30, TimeUnit.SECONDS);

            assertThat(created.getName()).endsWith("/functions/" + functionId);
            assertThat(client.getFunction(created.getName()).getState()).isEqualTo(Function.State.ACTIVE);
        }
    }
}

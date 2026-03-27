package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import software.amazon.awssdk.services.apigateway.model.RestApi;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerApiGatewayServiceTest extends AbstractFlociContainerServiceTest {

    static ApiGatewayClient apiGateway;

    @BeforeAll
    static void setUp() {
        apiGateway = client(ApiGatewayClient.builder());
    }

    @Test
    void shouldCreateAndListRestApi() {
        String apiName = "test-api-" + System.currentTimeMillis();

        String apiId = apiGateway.createRestApi(b -> b.name(apiName).description("Test API")).id();

        assertThat(apiId).isNotBlank();

        List<String> apiNames = apiGateway.getRestApis().items().stream()
                .map(RestApi::name)
                .toList();

        assertThat(apiNames).contains(apiName);
    }

    @Test
    void shouldCreateResource() {
        String apiName = "test-resource-" + System.currentTimeMillis();
        String apiId = apiGateway.createRestApi(b -> b.name(apiName)).id();

        String rootResourceId = apiGateway.getResources(b -> b.restApiId(apiId))
                .items().get(0).id();

        String resourceId = apiGateway.createResource(b -> b
                        .restApiId(apiId)
                        .parentId(rootResourceId)
                        .pathPart("test"))
                .id();

        assertThat(resourceId).isNotBlank();
    }

}

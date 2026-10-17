package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import software.amazon.awssdk.services.appsync.model.AuthenticationType;
import software.amazon.awssdk.services.appsync.model.GraphqlApi;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class AppSyncServiceTest extends AbstractServiceTest {

    static AppSyncClient appSync;
    static String apiId;

    private static final String API_NAME = "test-api-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        appSync = client(AppSyncClient.builder());
    }

    @Test
    @Order(1)
    void shouldListGraphqlApis() {
        List<GraphqlApi> apis = appSync.listGraphqlApis(b -> {}).graphqlApis();

        assertThat(apis).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateGraphqlApi() {
        apiId = appSync.createGraphqlApi(b -> b
                .name(API_NAME)
                .authenticationType(AuthenticationType.API_KEY))
                .graphqlApi()
                .apiId();

        assertThat(apiId).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldGetGraphqlApi() {
        GraphqlApi api = appSync.getGraphqlApi(b -> b.apiId(apiId)).graphqlApi();

        assertThat(api.name()).isEqualTo(API_NAME);
        assertThat(api.authenticationType()).isEqualTo(AuthenticationType.API_KEY);
    }

    @Test
    @Order(4)
    void shouldListGraphqlApisContainsCreatedApi() {
        List<GraphqlApi> apis = appSync.listGraphqlApis(b -> {}).graphqlApis();

        assertThat(apis).anyMatch(a -> a.apiId().equals(apiId));
    }

    @Test
    @Order(5)
    void shouldDeleteGraphqlApi() {
        appSync.deleteGraphqlApi(b -> b.apiId(apiId));

        List<GraphqlApi> apis = appSync.listGraphqlApis(b -> {}).graphqlApis();
        assertThat(apis).noneMatch(a -> a.apiId().equals(apiId));
    }
}

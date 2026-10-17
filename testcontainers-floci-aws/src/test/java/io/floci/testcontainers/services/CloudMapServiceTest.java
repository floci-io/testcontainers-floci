package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.servicediscovery.ServiceDiscoveryClient;
import software.amazon.awssdk.services.servicediscovery.model.NamespaceSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CloudMapServiceTest extends AbstractServiceTest {

    static ServiceDiscoveryClient cloudMap;

    @BeforeAll
    static void setUp() {
        cloudMap = client(ServiceDiscoveryClient.builder());
    }

    @Test
    void shouldListNamespaces() {
        List<NamespaceSummary> namespaces = cloudMap.listNamespaces(b -> {}).namespaces();

        assertThat(namespaces).isNotNull();
    }

    @Test
    void shouldCreateHttpNamespaceAndListIt() {
        String namespaceName = "test-ns-" + System.currentTimeMillis();

        var createResponse = cloudMap.createHttpNamespace(b -> b
                .name(namespaceName)
                .creatorRequestId("req-" + System.currentTimeMillis()));

        assertThat(createResponse.operationId()).isNotEmpty();

        List<String> namespaceNames = cloudMap.listNamespaces(b -> {}).namespaces().stream()
                .map(NamespaceSummary::name)
                .toList();

        assertThat(namespaceNames).contains(namespaceName);
    }
}

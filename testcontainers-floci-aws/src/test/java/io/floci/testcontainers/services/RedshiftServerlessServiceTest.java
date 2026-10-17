package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.redshiftserverless.RedshiftServerlessClient;
import software.amazon.awssdk.services.redshiftserverless.model.Namespace;
import software.amazon.awssdk.services.redshiftserverless.model.NamespaceStatus;
import software.amazon.awssdk.services.redshiftserverless.model.ResourceNotFoundException;
import software.amazon.awssdk.services.redshiftserverless.model.Tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@TestMethodOrder(OrderAnnotation.class)
class RedshiftServerlessServiceTest extends AbstractServiceTest {

    private static final String NAMESPACE_NAME = "floci-tc-namespace";

    static RedshiftServerlessClient redshiftServerless;

    static String namespaceArn;

    @BeforeAll
    static void setUp() {
        redshiftServerless = client(RedshiftServerlessClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateNamespace() {
        Namespace namespace = redshiftServerless.createNamespace(b -> b
                .namespaceName(NAMESPACE_NAME)
                .adminUsername("admin")
                .adminUserPassword("Secret123!")).namespace();

        assertThat(namespace.namespaceName()).isEqualTo(NAMESPACE_NAME);
        assertThat(namespace.status()).isEqualTo(NamespaceStatus.AVAILABLE);
        assertThat(namespace.namespaceArn()).contains(":redshift-serverless:");
        namespaceArn = namespace.namespaceArn();
    }

    @Test
    @Order(2)
    void shouldGetAndListNamespace() {
        assertThat(redshiftServerless.getNamespace(b -> b.namespaceName(NAMESPACE_NAME)).namespace().namespaceArn())
                .isEqualTo(namespaceArn);
        assertThat(redshiftServerless.listNamespaces(b -> {}).namespaces())
                .extracting(Namespace::namespaceName)
                .contains(NAMESPACE_NAME);
    }

    @Test
    @Order(3)
    void shouldTagNamespace() {
        redshiftServerless.tagResource(b -> b.resourceArn(namespaceArn)
                .tags(Tag.builder().key("env").value("test").build()));

        assertThat(redshiftServerless.listTagsForResource(b -> b.resourceArn(namespaceArn)).tags())
                .extracting(Tag::key, Tag::value)
                .contains(tuple("env", "test"));
    }

    @Test
    @Order(4)
    void shouldDeleteNamespace() {
        redshiftServerless.deleteNamespace(b -> b.namespaceName(NAMESPACE_NAME));

        assertThatThrownBy(() -> redshiftServerless.getNamespace(b -> b.namespaceName(NAMESPACE_NAME)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

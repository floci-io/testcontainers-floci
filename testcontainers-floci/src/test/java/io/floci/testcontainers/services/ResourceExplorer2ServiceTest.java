package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.resourceexplorer2.ResourceExplorer2Client;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ResourceExplorer2ServiceTest extends AbstractServiceTest {

    static ResourceExplorer2Client resourceExplorer2;

    static String viewArn;

    @BeforeAll
    static void setUp() {
        resourceExplorer2 = client(ResourceExplorer2Client.builder());
    }

    @Test
    @Order(1)
    void shouldHaveAutoProvisionedIndex() {
        var response = resourceExplorer2.getIndex(b -> {});

        assertThat(response.arn()).isNotBlank();
        assertThat(response.stateAsString()).isEqualTo("ACTIVE");
    }

    @Test
    @Order(2)
    void shouldHaveAutoProvisionedDefaultView() {
        var response = resourceExplorer2.getDefaultView(b -> {});

        assertThat(response.viewArn()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldCreateAndGetView() {
        var created = resourceExplorer2.createView(b -> b
                .viewName("floci-tc-view")
                .filters(f -> f.filterString("service:s3")));

        viewArn = created.view().viewArn();
        assertThat(viewArn).isNotBlank();

        var response = resourceExplorer2.getView(b -> b.viewArn(viewArn));
        assertThat(response.view().viewArn()).isEqualTo(viewArn);
    }

    @Test
    @Order(4)
    void shouldListViewsContainsCreatedView() {
        var response = resourceExplorer2.listViews(b -> {});

        assertThat(response.views()).contains(viewArn);
    }

    @Test
    @Order(5)
    void shouldDeleteView() {
        resourceExplorer2.deleteView(b -> b.viewArn(viewArn));

        var response = resourceExplorer2.listViews(b -> {});
        assertThat(response.views()).doesNotContain(viewArn);
    }
}

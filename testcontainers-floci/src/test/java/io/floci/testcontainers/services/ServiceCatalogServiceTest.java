package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.servicecatalog.ServiceCatalogClient;
import software.amazon.awssdk.services.servicecatalog.model.PortfolioDetail;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ServiceCatalogServiceTest extends AbstractServiceTest {

    static ServiceCatalogClient serviceCatalog;

    static String portfolioId;

    @BeforeAll
    static void setUp() {
        serviceCatalog = client(ServiceCatalogClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreatePortfolio() {
        var response = serviceCatalog.createPortfolio(b -> b
                .displayName("floci-tc-portfolio")
                .providerName("Floci"));

        assertThat(response.portfolioDetail().id()).isNotBlank();
        assertThat(response.portfolioDetail().displayName()).isEqualTo("floci-tc-portfolio");
        portfolioId = response.portfolioDetail().id();
    }

    @Test
    @Order(2)
    void shouldListPortfoliosContainsCreatedPortfolio() {
        var response = serviceCatalog.listPortfolios(b -> {});

        assertThat(response.portfolioDetails())
                .extracting(PortfolioDetail::id)
                .contains(portfolioId);
    }

    @Test
    @Order(3)
    void shouldDeletePortfolio() {
        serviceCatalog.deletePortfolio(b -> b.id(portfolioId));

        var response = serviceCatalog.listPortfolios(b -> {});
        assertThat(response.portfolioDetails())
                .extracting(PortfolioDetail::id)
                .doesNotContain(portfolioId);
    }
}

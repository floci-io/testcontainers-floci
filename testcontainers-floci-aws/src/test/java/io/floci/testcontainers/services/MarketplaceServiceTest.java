package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.marketplacecatalog.MarketplaceCatalogClient;

import static org.assertj.core.api.Assertions.assertThat;

class MarketplaceServiceTest extends AbstractServiceTest {

    static MarketplaceCatalogClient marketplaceCatalog;

    @BeforeAll
    static void setUp() {
        marketplaceCatalog = client(MarketplaceCatalogClient.builder());
    }

    @Test
    void shouldListCatalogEntities() {
        var response = marketplaceCatalog.listEntities(b -> b.catalog("AWSMarketplace").entityType("SaaSProduct"));

        assertThat(response.entitySummaryList()).isNotNull();
    }
}

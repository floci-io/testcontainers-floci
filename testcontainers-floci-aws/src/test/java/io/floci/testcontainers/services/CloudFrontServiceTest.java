package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class CloudFrontServiceTest extends AbstractServiceTest {

    private static final String CALLER_REFERENCE = "test-distribution-" + System.currentTimeMillis();
    private static String distributionId;

    static CloudFrontClient cloudFront;

    @BeforeAll
    static void setUp() {
        cloudFront = client(CloudFrontClient.builder());
    }

    @Test
    @Order(1)
    void shouldListDistributions() {
        DistributionList distributions = cloudFront.listDistributions().distributionList();

        assertThat(distributions).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateDistribution() {
        CreateDistributionResponse response = cloudFront.createDistribution(b -> b
                .distributionConfig(DistributionConfig.builder()
                        .callerReference(CALLER_REFERENCE)
                        .comment("Test distribution")
                        .enabled(true)
                        .origins(Origins.builder()
                                .quantity(1)
                                .items(Origin.builder()
                                        .id("test-origin")
                                        .domainName("example.com")
                                        .customOriginConfig(CustomOriginConfig.builder()
                                                .httpPort(80)
                                                .httpsPort(443)
                                                .originProtocolPolicy(OriginProtocolPolicy.HTTP_ONLY)
                                                .build())
                                        .build())
                                .build())
                        .defaultCacheBehavior(DefaultCacheBehavior.builder()
                                .targetOriginId("test-origin")
                                .viewerProtocolPolicy(ViewerProtocolPolicy.ALLOW_ALL)
                                .minTTL(0L)
                                .forwardedValues(ForwardedValues.builder()
                                        .queryString(false)
                                        .cookies(CookiePreference.builder()
                                                .forward(ItemSelection.NONE)
                                                .build())
                                        .build())
                                .trustedSigners(TrustedSigners.builder()
                                        .enabled(false)
                                        .quantity(0)
                                        .build())
                                .build())
                        .build()));

        assertThat(response.distribution()).isNotNull();
        assertThat(response.distribution().id()).isNotBlank();

        distributionId = response.distribution().id();
    }

    @Test
    @Order(3)
    void shouldGetDistribution() {
        assertThat(distributionId).isNotNull();

        GetDistributionResponse response = cloudFront.getDistribution(b -> b.id(distributionId));

        assertThat(response.distribution()).isNotNull();
        assertThat(response.distribution().id()).isEqualTo(distributionId);
        assertThat(response.distribution().distributionConfig().callerReference()).isEqualTo(CALLER_REFERENCE);
    }

    @Test
    @Order(4)
    void shouldUpdateDistribution() {
        GetDistributionResponse getResponse = cloudFront.getDistribution(b -> b.id(distributionId));
        String etag = getResponse.eTag();

        // Update the distribution (disable it before deletion)
        cloudFront.updateDistribution(b -> b
                .id(distributionId)
                .ifMatch(etag)
                .distributionConfig(getResponse.distribution().distributionConfig().toBuilder()
                        .enabled(false)
                        .comment("Updated test distribution")
                        .build()));

        GetDistributionResponse updatedResponse = cloudFront.getDistribution(b -> b.id(distributionId));

        assertThat(updatedResponse.distribution().distributionConfig().enabled()).isFalse();
        assertThat(updatedResponse.distribution().distributionConfig().comment()).isEqualTo("Updated test distribution");
    }

    @Test
    @Order(5)
    void shouldDeleteDistribution() {
        GetDistributionResponse getResponse = cloudFront.getDistribution(b -> b.id(distributionId));
        String etag = getResponse.eTag();

        // Delete the distribution
        cloudFront.deleteDistribution(b -> b.id(distributionId).ifMatch(etag));

        // Verify it's deleted by checking the list
        List<String> distributionIds = cloudFront.listDistributions().distributionList()
                .items().stream()
                .map(DistributionSummary::id)
                .toList();

        assertThat(distributionIds).doesNotContain(distributionId);
    }
}

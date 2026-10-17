package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.pricing.PricingClient;
import software.amazon.awssdk.services.pricing.model.DescribeServicesRequest;
import software.amazon.awssdk.services.pricing.model.DescribeServicesResponse;
import software.amazon.awssdk.services.pricing.model.Service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PricingServiceTest extends AbstractServiceTest {

    static PricingClient pricing;

    @BeforeAll
    static void setUp() {
        pricing = client(PricingClient.builder());
    }

    @Test
    void shouldDescribeServices() {
        DescribeServicesResponse response = pricing.describeServices(
                DescribeServicesRequest.builder().build());

        assertThat(response).isNotNull();
        assertThat(response.services()).isNotEmpty();
    }

    @Test
    void shouldDescribeSpecificService() {
        DescribeServicesResponse response = pricing.describeServices(
                DescribeServicesRequest.builder().serviceCode("AmazonEC2").build());

        assertThat(response).isNotNull();
        List<Service> services = response.services();
        assertThat(services).hasSize(1);
        assertThat(services.get(0).serviceCode()).isEqualTo("AmazonEC2");
        assertThat(services.get(0).attributeNames()).isNotEmpty();
    }
}

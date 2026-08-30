package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.servicequotas.ServiceQuotasClient;
import software.amazon.awssdk.services.servicequotas.model.ServiceQuota;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceQuotasServiceTest extends AbstractServiceTest {

    private static final String CONCURRENT_BUILDS_QUOTA = "L-2DC20C30";

    static ServiceQuotasClient serviceQuotas;

    @BeforeAll
    static void setUp() {
        serviceQuotas = client(ServiceQuotasClient.builder());
    }

    @Test
    void shouldListServiceQuotasForCodeBuild() {
        var response = serviceQuotas.listServiceQuotas(b -> b.serviceCode("codebuild"));

        assertThat(response.quotas())
                .extracting(ServiceQuota::quotaCode)
                .contains(CONCURRENT_BUILDS_QUOTA);
    }

    @Test
    void shouldGetServiceQuota() {
        var response = serviceQuotas.getServiceQuota(b -> b
                .serviceCode("codebuild")
                .quotaCode(CONCURRENT_BUILDS_QUOTA));

        assertThat(response.quota().quotaName()).isEqualTo("Concurrently running builds");
        assertThat(response.quota().value()).isGreaterThanOrEqualTo(60.0);
    }

    @Test
    void shouldListAwsDefaultServiceQuotas() {
        var response = serviceQuotas.listAWSDefaultServiceQuotas(b -> b.serviceCode("codebuild"));

        assertThat(response.quotas()).isNotEmpty();
    }
}

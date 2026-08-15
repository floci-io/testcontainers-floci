package io.floci.testcontainers.config.services;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class CloudFrontConfigTest {

    @Test
    void shouldApplyDefaultCloudFrontConfig() {
        CloudFrontConfig config = CloudFrontConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDomainSuffix()).isEqualTo("cloudfront.net");
        assertThat(config.getAllowedPrivateOriginHosts()).isEmpty();
    }

    @Test
    void shouldApplyCustomCloudFrontConfig() {
        CloudFrontConfig config = CloudFrontConfig.builder()
                .enabled(false)
                .domainSuffix("example.com")
                .allowedPrivateOriginHosts(List.of("internal.example.com", "private.example.com"))
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDomainSuffix()).isEqualTo("example.com");
        assertThat(config.getAllowedPrivateOriginHosts())
                .contains(List.of("internal.example.com", "private.example.com"));
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFrontConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFRONT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDFRONT_DOMAIN_SUFFIX", "cloudfront.net")
                .doesNotContainKey("FLOCI_SERVICES_CLOUDFRONT_ALLOWED_PRIVATE_ORIGIN_HOSTS");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFrontConfig.builder()
                .enabled(true)
                .domainSuffix("custom.example.net")
                .allowedPrivateOriginHosts(List.of("internal.example.com", "private.example.com"))
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_CLOUDFRONT_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_CLOUDFRONT_DOMAIN_SUFFIX", "custom.example.net")
                .containsEntry("FLOCI_SERVICES_CLOUDFRONT_ALLOWED_PRIVATE_ORIGIN_HOSTS",
                        "internal.example.com,private.example.com");
    }

    @Test
    void shouldNotApplyEmptyAllowedPrivateOriginHostsEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFrontConfig.builder()
                .allowedPrivateOriginHosts(List.of())
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).doesNotContainKey("FLOCI_SERVICES_CLOUDFRONT_ALLOWED_PRIVATE_ORIGIN_HOSTS");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        CloudFrontConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_CLOUDFRONT_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        CloudFrontConfig config = CloudFrontConfig.builder()
                .enabled(false)
                .domainSuffix("example.com")
                .allowedPrivateOriginHosts(List.of("internal.example.com"))
                .build();
        CloudFrontConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDomainSuffix()).isEqualTo("example.com");
        assertThat(copy.getAllowedPrivateOriginHosts()).contains(List.of("internal.example.com"));
    }

}

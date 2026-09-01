package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class S3ConfigTest {

    @Test
    void shouldApplyDefaultS3Config() {
        S3Config config = S3Config.builder().build();
        assertThat(config.isEnabled()).isTrue();
        assertThat(config.getDefaultPresignExpirySeconds()).isEqualTo(3600);
        assertThat(config.isEnforceAuth()).isFalse();
        assertThat(config.isGlobalBucketNamespace()).isFalse();
    }

    @Test
    void shouldApplyCustomS3Config() {
        S3Config config = S3Config.builder()
                .enabled(false)
                .defaultPresignExpirySeconds(7200)
                .enforceAuth(true)
                .globalBucketNamespace(true)
                .build();
        assertThat(config.isEnabled()).isFalse();
        assertThat(config.getDefaultPresignExpirySeconds()).isEqualTo(7200);
        assertThat(config.isEnforceAuth()).isTrue();
        assertThat(config.isGlobalBucketNamespace()).isTrue();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        S3Config.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_S3_ENABLED", "true")
                .containsEntry("FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", "3600")
                .containsEntry("FLOCI_SERVICES_S3_ENFORCE_AUTH", "false")
                .containsEntry("FLOCI_SERVICES_S3_GLOBAL_BUCKET_NAMESPACE", "false");
    }

    @Test
    void shouldApplyCustomEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        S3Config.builder()
                .defaultPresignExpirySeconds(7200)
                .enforceAuth(true)
                .globalBucketNamespace(true)
                .build()
                .applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap())
                .containsEntry("FLOCI_SERVICES_S3_DEFAULT_PRESIGN_EXPIRY_SECONDS", "7200")
                .containsEntry("FLOCI_SERVICES_S3_ENFORCE_AUTH", "true")
                .containsEntry("FLOCI_SERVICES_S3_GLOBAL_BUCKET_NAMESPACE", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        S3Config.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_S3_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        S3Config config = S3Config.builder()
                .enabled(false)
                .defaultPresignExpirySeconds(7200)
                .enforceAuth(true)
                .globalBucketNamespace(true)
                .build();
        S3Config copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
        assertThat(copy.getDefaultPresignExpirySeconds()).isEqualTo(7200);
        assertThat(copy.isEnforceAuth()).isTrue();
        assertThat(copy.isGlobalBucketNamespace()).isTrue();
    }

}

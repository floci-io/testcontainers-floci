package io.floci.testcontainers.config.services;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static io.floci.testcontainers.testing.ContainerUtils.genericContainer;
import static org.assertj.core.api.Assertions.assertThat;

class Route53ResolverConfigTest {

    @Test
    void shouldApplyDefaultRoute53ResolverConfig() {
        Route53ResolverConfig config = Route53ResolverConfig.builder().build();
        assertThat(config.isEnabled()).isTrue();
    }

    @Test
    void shouldApplyCustomRoute53ResolverConfig() {
        Route53ResolverConfig config = Route53ResolverConfig.builder()
                .enabled(false)
                .build();
        assertThat(config.isEnabled()).isFalse();
    }

    @Test
    void shouldApplyDefaultEnvVarsToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53ResolverConfig.builder().build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ROUTE53RESOLVER_ENABLED", "true");
    }

    @Test
    void shouldApplyDisabledEnvVarToContainer() {
        GenericContainer<?> container = genericContainer();
        Route53ResolverConfig.builder().enabled(false).build().applyEnvVarsToContainer(container);

        assertThat(container.getEnvMap()).containsEntry("FLOCI_SERVICES_ROUTE53RESOLVER_ENABLED", "false");
    }

    @Test
    void shouldPreserveValuesOnToBuilder() {
        Route53ResolverConfig config = Route53ResolverConfig.builder()
                .enabled(false)
                .build();
        Route53ResolverConfig copy = config.toBuilder().build();
        assertThat(copy.isEnabled()).isFalse();
    }
}

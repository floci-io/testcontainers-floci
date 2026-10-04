package io.floci.testcontainers.oci;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every configuration exposed by {@link FlociOciContainer} is actually picked up by the
 * container: the changed value survives the {@code with*Config(...)} round-trip, the matching
 * {@code FLOCI_OCI_*} environment variable is applied, and the container's port mappings are wired.
 *
 * <p>There is exactly one test method per service config class in {@code config/services/} plus one
 * per cross-cutting config class in {@code config/}.
 */
class FlociOciContainerServicesConfigTest {

    /**
     * Applies {@code configurer} to a fresh {@link FlociOciContainer}, then asserts that:
     * <ol>
     *   <li>the changed property is readable again via {@code get*Config()}
     *       ({@code actualValue} equals {@code expectedValue}),</li>
     *   <li>the container has the environment variable {@code envKey=envValue}, and</li>
     *   <li>the container exposes the main Floci OCI port.</li>
     * </ol>
     */
    private static void assertConfigWired(
            Consumer<FlociOciContainer> configurer,
            Function<FlociOciContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue) {
        try (FlociOciContainer container = new FlociOciContainer()) {
            configurer.accept(container);

            assertThat(actualValue.apply(container))
                    .as("value retrieved via get*Config()")
                    .isEqualTo(expectedValue);
            assertThat(container.getEnvMap())
                    .as("environment variable applied to the Floci OCI container")
                    .containsEntry(envKey, envValue);
            assertThat(container.getExposedPorts())
                    .as("port mapping configured on the Floci OCI container")
                    .contains(FlociOciContainer.PORT);
        }
    }

    // --- Service configs (config/services/) -------------------------------------------------------

    @Test
    void shouldWireObjectStorageConfigIntoContainer() {
        assertConfigWired(
                c -> c.withObjectStorageConfig(cfg -> cfg.enabled(false)),
                c -> c.getObjectStorageConfig().isEnabled(), false,
                "FLOCI_OCI_SERVICES_OBJECTSTORAGE_ENABLED", "false");
    }

    @Test
    void shouldWireQueueConfigIntoContainer() {
        assertConfigWired(
                c -> c.withQueueConfig(cfg -> cfg.enabled(false)),
                c -> c.getQueueConfig().isEnabled(), false,
                "FLOCI_OCI_SERVICES_QUEUE_ENABLED", "false");
    }

    @Test
    void shouldWireStreamingConfigIntoContainer() {
        assertConfigWired(
                c -> c.withStreamingConfig(cfg -> cfg.enabled(false)),
                c -> c.getStreamingConfig().isEnabled(), false,
                "FLOCI_OCI_SERVICES_STREAMING_ENABLED", "false");
    }

    // --- Cross-cutting configs (config/) ----------------------------------------------------------

    @Test
    void shouldWireAuthConfigIntoContainer() {
        assertConfigWired(
                c -> c.withAuthConfig(cfg -> cfg.requireSignature(true)),
                c -> c.getAuthConfig().isRequireSignature(), true,
                "FLOCI_OCI_AUTH_REQUIRE_SIGNATURE", "true");
    }

    @Test
    void shouldWireTlsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTlsConfig(cfg -> cfg.enabled(true)),
                c -> c.getTlsConfig().isEnabled(), true,
                "FLOCI_OCI_TLS_ENABLED", "true");
    }
}

package io.floci.testcontainers.gcp;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that every configuration exposed by {@link FlociGcpContainer} is actually picked up by the
 * container: the changed value survives the {@code with*Config(...)} round-trip, the matching
 * {@code FLOCI_GCP_*} environment variable is applied, and the container's port mappings are wired.
 *
 * <p>There is exactly one test method per service config class in {@code config/services/} plus one
 * per cross-cutting config class in {@code config/}.
 */
class FlociGcpContainerServicesConfigTest {

    /**
     * Applies {@code configurer} to a fresh {@link FlociGcpContainer}, then asserts that:
     * <ol>
     *   <li>the changed property is readable again via {@code get*Config()}
     *       ({@code actualValue} equals {@code expectedValue}),</li>
     *   <li>the container has the environment variable {@code envKey=envValue}, and</li>
     *   <li>the container exposes the main Floci GCP port.</li>
     * </ol>
     */
    private static void assertConfigWired(
            Consumer<FlociGcpContainer> configurer,
            Function<FlociGcpContainer, Object> actualValue,
            Object expectedValue,
            String envKey,
            String envValue) {
        try (FlociGcpContainer container = new FlociGcpContainer()) {
            configurer.accept(container);

            assertThat(actualValue.apply(container))
                    .as("value retrieved via get*Config()")
                    .isEqualTo(expectedValue);
            assertThat(container.getEnvMap())
                    .as("environment variable applied to the Floci GCP container")
                    .containsEntry(envKey, envValue);
            assertThat(container.getExposedPorts())
                    .as("port mapping configured on the Floci GCP container")
                    .contains(FlociGcpContainer.PORT);
        }
    }

    // --- Service configs (config/services/) -------------------------------------------------------

    @Test
    void shouldWirePubSubConfigIntoContainer() {
        assertConfigWired(
                c -> c.withPubSubConfig(cfg -> cfg.enabled(false)),
                c -> c.getPubSubConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_PUBSUB_ENABLED", "false");
    }

    @Test
    void shouldWireFirestoreConfigIntoContainer() {
        assertConfigWired(
                c -> c.withFirestoreConfig(cfg -> cfg.enabled(false)),
                c -> c.getFirestoreConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_FIRESTORE_ENABLED", "false");
    }

    @Test
    void shouldWireDatastoreConfigIntoContainer() {
        assertConfigWired(
                c -> c.withDatastoreConfig(cfg -> cfg.enabled(false)),
                c -> c.getDatastoreConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_DATASTORE_ENABLED", "false");
    }

    @Test
    void shouldWireIamCredentialsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withIamCredentialsConfig(cfg -> cfg.enabled(false)),
                c -> c.getIamCredentialsConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_IAMCREDENTIALS_ENABLED", "false");
    }

    @Test
    void shouldWireStsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withStsConfig(cfg -> cfg.enabled(false)),
                c -> c.getStsConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_STS_ENABLED", "false");
    }

    @Test
    void shouldWireSecretManagerConfigIntoContainer() {
        assertConfigWired(
                c -> c.withSecretManagerConfig(cfg -> cfg.enabled(false)),
                c -> c.getSecretManagerConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_SECRETMANAGER_ENABLED", "false");
    }

    @Test
    void shouldWireLoggingConfigIntoContainer() {
        assertConfigWired(
                c -> c.withLoggingConfig(cfg -> cfg.enabled(false)),
                c -> c.getLoggingConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_LOGGING_ENABLED", "false");
    }

    @Test
    void shouldWireKmsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withKmsConfig(cfg -> cfg.enabled(false)),
                c -> c.getKmsConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_KMS_ENABLED", "false");
    }

    @Test
    void shouldWireCloudTasksConfigIntoContainer() {
        assertConfigWired(
                c -> c.withCloudTasksConfig(cfg -> cfg.enabled(false)),
                c -> c.getCloudTasksConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_CLOUDTASKS_ENABLED", "false");
    }

    @Test
    void shouldWireMonitoringConfigIntoContainer() {
        assertConfigWired(
                c -> c.withMonitoringConfig(cfg -> cfg.enabled(false)),
                c -> c.getMonitoringConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_MONITORING_ENABLED", "false");
    }

    @Test
    void shouldWireEventarcConfigIntoContainer() {
        assertConfigWired(
                c -> c.withEventarcConfig(cfg -> cfg.enabled(false)),
                c -> c.getEventarcConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_EVENTARC_ENABLED", "false");
    }

    @Test
    void shouldWireFirebaseAuthConfigIntoContainer() {
        assertConfigWired(
                c -> c.withFirebaseAuthConfig(cfg -> cfg.enabled(false)),
                c -> c.getFirebaseAuthConfig().isEnabled(), false,
                "FLOCI_GCP_SERVICES_FIREBASEAUTH_ENABLED", "false");
    }

    // --- Cross-cutting configs (config/) -----------------------------------------------------------

    @Test
    void shouldWireTlsConfigIntoContainer() {
        assertConfigWired(
                c -> c.withTlsConfig(cfg -> cfg.enabled(true)),
                c -> c.getTlsConfig().isEnabled(), true,
                "FLOCI_GCP_TLS_ENABLED", "true");
    }
}

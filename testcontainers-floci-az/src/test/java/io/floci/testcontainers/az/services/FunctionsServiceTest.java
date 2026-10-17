package io.floci.testcontainers.az.services;

import io.floci.testcontainers.az.FlociAzContainer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionsServiceTest extends AbstractServiceTest {

    @Test
    void shouldDeployFunction() throws IOException {
        String app = "/" + floci.getAccountName() + "-functions/admin/apps/app-" + UUID.randomUUID().toString().substring(0, 8);

        RestResponse createdApp = rest("PUT", app, "{\"runtime\":\"node\"}");
        RestResponse deployed = rest("PUT", app + "/functions/hello", deployBody());
        RestResponse fetched = rest("GET", app + "/functions/hello", null);

        assertThat(createdApp.status()).as(createdApp.toString()).isEqualTo(201);
        assertThat(deployed.status()).as(deployed.toString()).isEqualTo(201);
        assertThat(fetched.json().path("status").asText()).isEqualTo("Ready");
    }

    @Test
    void shouldReturnStubInvocationResultWhenMocked() throws IOException {
        try (FlociAzContainer mockedFloci = new FlociAzContainer(IMAGE)
                .disableAllServices()
                .withFunctionsConfig(c -> c.enabled(true).mocked(true))) {
            mockedFloci.start();
            String base = "/" + mockedFloci.getAccountName() + "-functions";

            rest(mockedFloci, "PUT", base + "/admin/apps/app", "{\"runtime\":\"node\"}");
            rest(mockedFloci, "PUT", base + "/admin/apps/app/functions/hello", deployBody());
            RestResponse invoked = rest(mockedFloci, "GET", base + "/api/app/hello", null);

            assertThat(invoked.status()).as(invoked.toString()).isEqualTo(200);
            assertThat(invoked.json().path("mocked").asBoolean()).isTrue();
        }
    }

    private static String deployBody() throws IOException {
        ByteArrayOutputStream zip = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(zip)) {
            addZipEntry(out, "function.json", """
                    {"bindings": [
                      {"authLevel": "anonymous", "type": "httpTrigger", "direction": "in", "name": "req", "methods": ["get"]},
                      {"type": "http", "direction": "out", "name": "res"}
                    ]}
                    """);
            addZipEntry(out, "index.js", """
                    module.exports = async function (context, req) {
                        context.res = { status: 200, body: "Hello!" };
                    };
                    """);
        }
        return "{\"handler\":\"index.handler\",\"timeoutSeconds\":60,\"zipBase64\":\""
                + Base64.getEncoder().encodeToString(zip.toByteArray()) + "\"}";
    }

    private static void addZipEntry(ZipOutputStream out, String name, String content) throws IOException {
        out.putNextEntry(new ZipEntry(name));
        out.write(content.getBytes(StandardCharsets.UTF_8));
        out.closeEntry();
    }
}

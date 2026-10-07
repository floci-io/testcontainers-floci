package io.floci.testcontainers.gcp.services;

import com.google.cloud.run.v2.Container;
import com.google.cloud.run.v2.ContainerPort;
import com.google.cloud.run.v2.CreateServiceRequest;
import com.google.cloud.run.v2.LocationName;
import com.google.cloud.run.v2.RevisionTemplate;
import com.google.cloud.run.v2.Service;
import com.google.cloud.run.v2.ServicesClient;
import com.google.cloud.run.v2.ServicesSettings;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class CloudRunServiceTest extends AbstractServiceTest {

    @Test
    void shouldDeployAndInvokeService() throws Exception {
        String serviceId = uniqueName("hello");

        try (ServicesClient client = ServicesClient.create(ServicesSettings.newHttpJsonBuilder()
                .setEndpoint(floci.getEndpoint())
                .setCredentialsProvider(noCredentials())
                .build())) {
            Service service = client.createServiceAsync(CreateServiceRequest.newBuilder()
                            .setParent(LocationName.of(projectId(), "us-central1").toString())
                            .setServiceId(serviceId)
                            .setService(Service.newBuilder()
                                    .setTemplate(RevisionTemplate.newBuilder()
                                            .addContainers(Container.newBuilder()
                                                    .setImage("nginx:alpine")
                                                    .addPorts(ContainerPort.newBuilder().setContainerPort(80)))))
                            .build())
                    .get(240, TimeUnit.SECONDS);

            try {
                // services are invoked through the main Floci GCP port, addressed by the Host header
                String response = get(URI.create(service.getUri()).getAuthority());

                assertThat(response).startsWith("HTTP/1.1 200").contains("Welcome to nginx");
            } finally {
                client.deleteServiceAsync(service.getName()).get(60, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Sends a plain HTTP request with the given {@code Host} header, which the JDK HTTP clients do not allow to set.
     */
    private static String get(String host) throws Exception {
        try (Socket socket = new Socket(floci.getHost(), floci.getMappedPort(floci.getPort()))) {
            socket.setSoTimeout(5000);
            OutputStream out = socket.getOutputStream();
            out.write(("GET / HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n")
                    .getBytes(StandardCharsets.US_ASCII));
            out.flush();
            InputStream in = socket.getInputStream();
            ByteArrayOutputStream response = new ByteArrayOutputStream();
            in.transferTo(response);
            return response.toString(StandardCharsets.UTF_8);
        }
    }
}

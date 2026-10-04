package io.floci.testcontainers.gcp.services;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class CloudSqlServiceTest extends AbstractServiceTest {

    private static final Pattern IP_ADDRESS = Pattern.compile("\"ipAddress\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern PORT = Pattern.compile("\"port\"\\s*:\\s*(\\d+)");

    @Test
    void shouldCreateReachablePostgresInstance() throws Exception {
        String instances = "/sql/v1beta4/projects/" + projectId() + "/instances";
        String instanceId = uniqueName("pg");

        try {
            RestResponse created = rest("POST", instances, "application/json", """
                    {
                      "name": "%s",
                      "region": "us-central1",
                      "databaseVersion": "POSTGRES_17",
                      "settings": {"tier": "db-custom-1-3840"}
                    }
                    """.formatted(instanceId));
            RestResponse fetched = rest("GET", instances + "/" + instanceId, null, null);

            assertThat(created.status()).as(created.body()).isEqualTo(200);
            assertThat(fetched.body()).contains("\"state\":\"RUNNABLE\"");

            // the database container's port is published on the Docker host
            Matcher ipAddress = IP_ADDRESS.matcher(fetched.body());
            Matcher port = PORT.matcher(fetched.body());
            assertThat(ipAddress.find() && port.find()).as(fetched.body()).isTrue();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipAddress.group(1), Integer.parseInt(port.group(1))), 5000);
                assertThat(socket.isConnected()).isTrue();
            }
        } finally {
            rest("DELETE", instances + "/" + instanceId, null, null);
        }
    }
}

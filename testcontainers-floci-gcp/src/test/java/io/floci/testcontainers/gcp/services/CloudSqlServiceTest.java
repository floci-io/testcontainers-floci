package io.floci.testcontainers.gcp.services;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sqladmin.SQLAdmin;
import com.google.api.services.sqladmin.model.DatabaseInstance;
import com.google.api.services.sqladmin.model.IpMapping;
import com.google.api.services.sqladmin.model.Operation;
import com.google.api.services.sqladmin.model.Settings;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;

class CloudSqlServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateReachablePostgresInstance() throws Exception {
        SQLAdmin client = sqlAdminClient();
        String instanceId = uniqueName("pg");

        try {
            Operation created = client.instances().insert(projectId(), new DatabaseInstance()
                            .setName(instanceId)
                            .setRegion("us-central1")
                            .setDatabaseVersion("POSTGRES_17")
                            .setSettings(new Settings().setTier("db-custom-1-3840")))
                    .execute();
            DatabaseInstance fetched = client.instances().get(projectId(), instanceId).execute();

            assertThat(created.getStatus()).isEqualTo("DONE");
            assertThat(fetched.getState()).isEqualTo("RUNNABLE");

            // the database container's port is published on the Docker host; Floci reports it in the
            // non-standard "port" field of the IP mapping
            IpMapping ipMapping = fetched.getIpAddresses().get(0);
            Number port = (Number) ipMapping.get("port");
            assertThat(port).as(ipMapping.toString()).isNotNull();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipMapping.getIpAddress(), port.intValue()), 5000);
                assertThat(socket.isConnected()).isTrue();
            }
        } finally {
            client.instances().delete(projectId(), instanceId).execute();
        }
    }

    private static SQLAdmin sqlAdminClient() {
        // instances.insert only answers once the database container accepts connections
        return new SQLAdmin.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                request -> request.setReadTimeout(120_000))
                .setApplicationName("testcontainers-floci")
                .setRootUrl(floci.getEndpoint() + "/")
                // the request paths of the generated client already start with sql/v1beta4/
                .setServicePath("")
                .build();
    }
}

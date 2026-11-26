package io.floci.testcontainers.oci.services;

import com.oracle.bmc.psql.PostgresqlClient;
import com.oracle.bmc.psql.model.ConnectionDetails;
import com.oracle.bmc.psql.model.CreateDbSystemDetails;
import com.oracle.bmc.psql.model.Credentials;
import com.oracle.bmc.psql.model.DbSystem;
import com.oracle.bmc.psql.model.NetworkDetails;
import com.oracle.bmc.psql.model.OciOptimizedStorageDetails;
import com.oracle.bmc.psql.model.OperationStatus;
import com.oracle.bmc.psql.model.PlainTextPasswordDetails;
import com.oracle.bmc.psql.model.ShapeSummary;
import com.oracle.bmc.psql.requests.CreateDbSystemRequest;
import com.oracle.bmc.psql.requests.DeleteDbSystemRequest;
import com.oracle.bmc.psql.requests.GetConnectionDetailsRequest;
import com.oracle.bmc.psql.requests.GetDbSystemRequest;
import com.oracle.bmc.psql.requests.GetWorkRequestRequest;
import com.oracle.bmc.psql.requests.ListShapesRequest;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class PsqlServiceTest extends AbstractServiceTest {

    private static final String USER = "admin_user";
    private static final String PASSWORD = "BEstrO0ng_#11";

    @Test
    void shouldListShapes() {
        try (PostgresqlClient postgresql = client(PostgresqlClient.builder())) {
            var shapes = postgresql.listShapes(ListShapesRequest.builder()
                    .compartmentId(tenancyId())
                    .build()).getShapeCollection().getItems();

            assertThat(shapes).extracting(ShapeSummary::getShape).contains("VM.Standard.E4.Flex");
        }
    }

    @Test
    void shouldConnectToDbSystem() throws Exception {
        // a dedicated container: while Floci OCI runs in a container, GetConnectionDetails returns the PostgreSQL
        // container's name unless the published host port is requested
        try (FlociOciContainer container = new FlociOciContainer(IMAGE)
                .withEnv("FLOCI_OCI_DOCKER_ENDPOINT_MODE", "published")) {
            container.start();

            try (PostgresqlClient postgresql = client(container, PostgresqlClient.builder())) {
                String dbSystemId = createDbSystem(postgresql, container.getTenancyId());
                try {
                    ConnectionDetails connection = postgresql.getConnectionDetails(GetConnectionDetailsRequest.builder()
                            .dbSystemId(dbSystemId)
                            .build()).getConnectionDetails();

                    try (Connection jdbc = connect(connection);
                         Statement statement = jdbc.createStatement();
                         ResultSet version = statement.executeQuery("SHOW server_version")) {
                        assertThat(version.next()).isTrue();
                        assertThat(version.getString(1)).startsWith("16");
                    }
                } finally {
                    postgresql.deleteDbSystem(DeleteDbSystemRequest.builder().dbSystemId(dbSystemId).build());
                }
            }
        }
    }

    /**
     * Creation runs in the background: the DB system becomes {@code ACTIVE} and its work request
     * {@code SUCCEEDED} once PostgreSQL accepts connections.
     */
    private static String createDbSystem(PostgresqlClient postgresql, String compartmentId) {
        var created = postgresql.createDbSystem(CreateDbSystemRequest.builder()
                .createDbSystemDetails(CreateDbSystemDetails.builder()
                        .compartmentId(compartmentId)
                        .displayName("db-" + System.nanoTime())
                        .dbVersion("16")
                        .shape("PostgreSQL.VM.Standard.E4.Flex.2.32GB")
                        .storageDetails(OciOptimizedStorageDetails.builder().isRegionallyDurable(true).build())
                        .networkDetails(NetworkDetails.builder().subnetId("ocid1.subnet.oc1..testcontainers").build())
                        .credentials(Credentials.builder()
                                .username(USER)
                                .passwordDetails(PlainTextPasswordDetails.builder().password(PASSWORD).build())
                                .build())
                        .build())
                .build());
        String dbSystemId = created.getDbSystem().getId();

        await().atMost(Duration.ofMinutes(3)).pollInterval(Duration.ofSeconds(1)).until(() -> postgresql
                .getWorkRequest(GetWorkRequestRequest.builder().workRequestId(created.getOpcWorkRequestId()).build())
                .getWorkRequest().getStatus(), status -> status != OperationStatus.InProgress
                && status != OperationStatus.Accepted);
        assertThat(postgresql.getDbSystem(GetDbSystemRequest.builder().dbSystemId(dbSystemId).build())
                .getDbSystem().getLifecycleState()).isEqualTo(DbSystem.LifecycleState.Active);
        return dbSystemId;
    }

    /**
     * Connects with {@code sslmode=verify-full} against the DB system's own self-signed certificate.
     */
    private static Connection connect(ConnectionDetails connection) throws Exception {
        Path caCertificate = Files.createTempFile("floci-psql-ca", ".pem");
        caCertificate.toFile().deleteOnExit();
        Files.writeString(caCertificate, connection.getCaCertificate());
        String url = "jdbc:postgresql://" + connection.getPrimaryDbEndpoint().getFqdn() + ":"
                + connection.getPrimaryDbEndpoint().getPort() + "/postgres?sslmode=verify-full&sslrootcert="
                + caCertificate.toAbsolutePath();

        Properties properties = new Properties();
        properties.setProperty("user", USER);
        properties.setProperty("password", PASSWORD);
        properties.setProperty("connectTimeout", "10");
        return DriverManager.getConnection(url, properties);
    }
}

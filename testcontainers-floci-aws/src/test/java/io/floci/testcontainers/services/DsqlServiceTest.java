package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dsql.DsqlClient;
import software.amazon.awssdk.services.dsql.model.ClusterStatus;
import software.amazon.awssdk.services.dsql.model.CreateClusterResponse;
import software.amazon.awssdk.services.dsql.model.GetClusterResponse;
import software.amazon.awssdk.services.dsql.model.ValidationException;

import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Aurora DSQL control plane and a JDBC connection to a cluster's database, which Floci runs in a PostgreSQL container.
 * Floci serves every cluster on one port and picks the cluster by the TLS server name the client sends, so the
 * connection goes to the container's mapped DSQL port with the cluster's endpoint as server name.
 */
@TestMethodOrder(OrderAnnotation.class)
class DsqlServiceTest extends AbstractServiceTest {

    private static final String SERVER_NAME_PROPERTY = "dsqlServerName";

    static DsqlClient dsql;
    static String identifier;
    static String endpoint;

    @BeforeAll
    static void setUp() {
        dsql = client(DsqlClient.builder());
    }

    @AfterAll
    static void tearDown() {
        if (identifier != null) {
            dsql.deleteCluster(b -> b.identifier(identifier));
            dsql.waiter().waitUntilClusterNotExists(b -> b.identifier(identifier),
                    w -> w.waitTimeout(Duration.ofMinutes(1)));
        }
    }

    @Test
    @Order(1)
    void shouldCreateClusterAndWaitUntilActive() {
        CreateClusterResponse created = dsql.createCluster(b -> b
                .deletionProtectionEnabled(false)
                .tags(Map.of("Name", "testcontainers-dsql")));
        identifier = created.identifier();
        assertThat(created.status()).isEqualTo(ClusterStatus.CREATING);

        GetClusterResponse active = dsql.waiter()
                .waitUntilClusterActive(b -> b.identifier(identifier), w -> w.waitTimeout(Duration.ofMinutes(3)))
                .matched().response().orElseThrow();
        endpoint = active.endpoint();

        assertThat(active.status()).isEqualTo(ClusterStatus.ACTIVE);
        assertThat(endpoint).startsWith(identifier + ".dsql." + floci.getRegion() + ".");
        assertThat(dsql.listClusters(b -> { }).clusters())
                .anySatisfy(summary -> assertThat(summary.identifier()).isEqualTo(identifier));
        assertThat(dsql.listTagsForResource(b -> b.resourceArn(active.arn())).tags())
                .containsEntry("Name", "testcontainers-dsql");
    }

    @Test
    @Order(2)
    void shouldNotDeleteProtectedCluster() {
        dsql.updateCluster(b -> b.identifier(identifier).deletionProtectionEnabled(true));

        assertThatThrownBy(() -> dsql.deleteCluster(b -> b.identifier(identifier)))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Deletion protection is enabled");

        dsql.updateCluster(b -> b.identifier(identifier).deletionProtectionEnabled(false));
    }

    @Test
    @Order(3)
    void shouldConnectAsAdminWithSdkToken() throws Exception {
        String token = dsql.utilities().generateDbConnectAdminAuthToken(b -> b
                .hostname(endpoint)
                .region(Region.of(floci.getRegion())));

        Properties properties = new Properties();
        properties.setProperty("user", "admin");
        properties.setProperty("password", token);
        properties.setProperty("sslmode", "require");
        properties.setProperty("sslfactory", ServerNameSocketFactory.class.getName());
        properties.setProperty(SERVER_NAME_PROPERTY, endpoint);
        String url = "jdbc:postgresql://" + floci.getHost() + ":"
                + floci.getMappedPort(floci.getDsqlConfig().getPort()) + "/postgres";

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE orders (id uuid PRIMARY KEY, total int)");
            statement.execute("INSERT INTO orders VALUES (gen_random_uuid(), 42)");
            try (ResultSet row = statement.executeQuery("SELECT current_user, total FROM orders")) {
                assertThat(row.next()).isTrue();
                assertThat(row.getString(1)).isEqualTo("admin");
                assertThat(row.getInt(2)).isEqualTo(42);
            }
        }
    }

    /**
     * Opens TLS to the host pgjdbc dialled, sending the cluster's endpoint as server name. The test trusts any
     * certificate, since it only needs Floci to pick the cluster.
     */
    public static final class ServerNameSocketFactory extends SSLSocketFactory {

        private final SSLSocketFactory delegate;
        private final String serverName;

        public ServerNameSocketFactory(Properties info) throws Exception {
            this.serverName = info.getProperty(SERVER_NAME_PROPERTY);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, null);
            this.delegate = context.getSocketFactory();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            SSLSocket tls = (SSLSocket) delegate.createSocket(socket, serverName, port, autoClose);
            SSLParameters parameters = tls.getSSLParameters();
            parameters.setServerNames(List.of(new SNIHostName(serverName)));
            tls.setSSLParameters(parameters);
            return tls;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return delegate.createSocket(host, port);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            return delegate.createSocket(host, port, localHost, localPort);
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return delegate.createSocket(host, port);
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
                throws IOException {
            return delegate.createSocket(address, port, localAddress, localPort);
        }
    }
}

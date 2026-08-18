package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.http.apache5.Apache5HttpClient;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBCluster;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class RdsDataServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_ID = "test-aurora-mysql-" + System.currentTimeMillis();
    private static final String MASTER_USER = "admin";
    private static final String MASTER_PASSWORD = "password123";
    private static final String DB_NAME = "testdb";

    static RdsClient rds;
    static RdsDataClient rdsData;
    static String clusterArn;

    @BeforeAll
    static void setUp() {
        // CreateDBCluster synchronously pulls and starts a real MySQL container, which can take
        // longer than the SDK's default ~30s socket read timeout, so extend it here.
        rds = client(RdsClient.builder()
                .httpClientBuilder(Apache5HttpClient.builder().socketTimeout(Duration.ofMinutes(3))));
        rdsData = client(RdsDataClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateAuroraCluster() {
        rds.createDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .engine("aurora-mysql")
                .engineMode("serverless")
                .masterUsername(MASTER_USER)
                .masterUserPassword(MASTER_PASSWORD)
                .databaseName(DB_NAME)
                .enableHttpEndpoint(true));

        rds.waiter().waitUntilDBClusterAvailable(b -> b.dbClusterIdentifier(CLUSTER_ID),
                c -> c.waitTimeout(Duration.ofSeconds(30)));

        DBCluster cluster = rds.describeDBClusters(b -> b.dbClusterIdentifier(CLUSTER_ID)).dbClusters().get(0);
        clusterArn = cluster.dbClusterArn();
        assertThat(clusterArn).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldExecuteStatement() {
        // The cluster reports as available as soon as its backing MySQL container has started,
        // but MySQL itself can take a bit longer after that to accept connections - retry until
        // it's actually ready instead of failing on the first "communications link failure".
        await().atMost(Duration.ofSeconds(30))
                .pollInterval(Duration.ofSeconds(2))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    ExecuteStatementResponse response = rdsData.executeStatement(b -> b
                            .resourceArn(clusterArn)
                            .secretArn("arn:aws:secretsmanager:us-east-1:000000000000:secret:rds-secret")
                            .database(DB_NAME)
                            .sql("SELECT 1 AS value"));

                    List<List<Field>> records = response.records();
                    assertThat(records).isNotNull();
                });
    }

    @Test
    @Order(3)
    void shouldExecuteDdlStatement() {
        rdsData.executeStatement(b -> b
                .resourceArn(clusterArn)
                .secretArn("arn:aws:secretsmanager:us-east-1:000000000000:secret:rds-secret")
                .database(DB_NAME)
                .sql("CREATE TABLE IF NOT EXISTS greetings (id INT AUTO_INCREMENT PRIMARY KEY, message TEXT)"));

        ExecuteStatementResponse response = rdsData.executeStatement(b -> b
                .resourceArn(clusterArn)
                .secretArn("arn:aws:secretsmanager:us-east-1:000000000000:secret:rds-secret")
                .database(DB_NAME)
                .sql("INSERT INTO greetings (message) VALUES ('hello from floci rds data')"));

        assertThat(response.numberOfRecordsUpdated()).isEqualTo(1L);
    }

    @Test
    @Order(4)
    void shouldDeleteCluster() {
        rds.deleteDBCluster(b -> b
                .dbClusterIdentifier(CLUSTER_ID)
                .skipFinalSnapshot(true));

        List<DBCluster> clusters = rds.describeDBClusters().dbClusters();
        assertThat(clusters).noneMatch(c -> c.dbClusterIdentifier().equals(CLUSTER_ID));
    }
}

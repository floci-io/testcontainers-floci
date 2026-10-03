package io.floci.testcontainers.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.DescribeStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.GetStatementResultResponse;
import software.amazon.awssdk.services.redshiftdata.model.StatusString;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class RedshiftDataServiceTest extends AbstractServiceTest {

    private static final String CLUSTER_ID = "floci-tc-redshift-data";
    private static final String MASTER_USER = "admin";
    private static final String MASTER_PASSWORD = "Password123";
    private static final String DATABASE = "dev";

    static RedshiftClient redshift;
    static RedshiftDataClient redshiftData;

    @BeforeAll
    static void setUp() {
        redshift = client(RedshiftClient.builder());
        redshiftData = client(RedshiftDataClient.builder());

        redshift.createCluster(b -> b
                .clusterIdentifier(CLUSTER_ID)
                .nodeType("dc2.large")
                .masterUsername(MASTER_USER)
                .masterUserPassword(MASTER_PASSWORD)
                .dbName(DATABASE));

        await().atMost(Duration.ofSeconds(120)).pollInterval(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(redshift.describeClusters(b -> b.clusterIdentifier(CLUSTER_ID)).clusters().get(0).clusterStatus())
                        .isEqualTo("available"));
    }

    @AfterAll
    static void tearDown() {
        redshift.deleteCluster(b -> b.clusterIdentifier(CLUSTER_ID).skipFinalClusterSnapshot(true));
    }

    @Test
    @Order(1)
    void shouldCreateTableAndInsertRows() {
        execute("CREATE TABLE tc_items (id int, name varchar(20))");
        String insertId = execute("INSERT INTO tc_items VALUES (1, 'a'), (2, 'b')");

        assertThat(describe(insertId).resultRows()).isEqualTo(2L);
    }

    @Test
    @Order(2)
    void shouldGetStatementResult() {
        String selectId = execute("SELECT id, name FROM tc_items ORDER BY id");

        GetStatementResultResponse result = redshiftData.getStatementResult(b -> b.id(selectId));
        assertThat(result.totalNumRows()).isEqualTo(2L);
        assertThat(result.records().get(0).get(0).longValue()).isEqualTo(1L);
        assertThat(result.records().get(1).get(1).stringValue()).isEqualTo("b");
    }

    private static String execute(String sql) {
        String id = redshiftData.executeStatement(b -> b
                        .clusterIdentifier(CLUSTER_ID)
                        .dbUser(MASTER_USER)
                        .database(DATABASE)
                        .sql(sql))
                .id();

        await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofMillis(250)).untilAsserted(() ->
                assertThat(describe(id).status()).isEqualTo(StatusString.FINISHED));
        return id;
    }

    private static DescribeStatementResponse describe(String id) {
        return redshiftData.describeStatement(b -> b.id(id));
    }
}

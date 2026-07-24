package io.floci.testcontainers.services;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.memorydb.MemoryDbClient;
import software.amazon.awssdk.services.memorydb.model.Cluster;
import software.amazon.awssdk.utils.builder.SdkBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class MemoryDbServiceTest extends AbstractServiceTest {

    static MemoryDbClient memoryDb;

    private static final String CLUSTER_NAME = "test-cluster-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        memoryDb = client(MemoryDbClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateCluster() {
        memoryDb.createCluster(b -> b
                .clusterName(CLUSTER_NAME)
                .nodeType("db.t4g.small")
                .aclName("open-access"));

        await().atMost(Duration.ofSeconds(60))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    Cluster cluster = memoryDb.describeClusters(b -> b.clusterName(CLUSTER_NAME))
                            .clusters().get(0);
                    assertThat(cluster.status()).isEqualTo("available");
                });
    }

    @Test
    @Order(2)
    void shouldStoreAndRetrieveDataViaRedis() {
        int proxyPort = floci.getMappedPort(floci.getMemoryDbConfig().getProxyBasePort());
        String redisUri = String.format("redis://%s:%d", floci.getHost(), proxyPort);

        // Wait for the cluster proxy to be reachable
        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    try (RedisClient rc = RedisClient.create(redisUri);
                         StatefulRedisConnection<String, String> conn = rc.connect()) {
                        assertThat(conn.sync().ping()).isEqualTo("PONG");
                    }
                });

        try (RedisClient redisClient = RedisClient.create(redisUri);
             StatefulRedisConnection<String, String> connection = redisClient.connect()) {

            RedisCommands<String, String> commands = connection.sync();

            commands.set("greeting", "hello from floci");
            assertThat(commands.get("greeting")).isEqualTo("hello from floci");

            commands.hset("user:1", Map.of(
                    "name", "Alice",
                    "email", "alice@example.com"));
            assertThat(commands.hget("user:1", "name")).isEqualTo("Alice");
            assertThat(commands.hgetall("user:1")).containsEntry("email", "alice@example.com");
        }
    }

    @Test
    @Order(3)
    void shouldDeleteCluster() {
        memoryDb.deleteCluster(b -> b.clusterName(CLUSTER_NAME));

        List<Cluster> clusters = memoryDb.describeClusters(SdkBuilder::build).clusters();
        assertThat(clusters).noneMatch(c -> c.name().equals(CLUSTER_NAME));
    }
}

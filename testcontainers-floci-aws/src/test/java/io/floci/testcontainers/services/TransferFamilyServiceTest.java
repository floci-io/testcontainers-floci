package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.transfer.TransferClient;
import software.amazon.awssdk.services.transfer.model.ListedServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TransferFamilyServiceTest extends AbstractServiceTest {

    static TransferClient transfer;

    @BeforeAll
    static void setUp() {
        transfer = client(TransferClient.builder());
    }

    @Test
    void shouldListServers() {
        List<ListedServer> servers = transfer.listServers(b -> {}).servers();

        assertThat(servers).isNotNull();
    }

    @Test
    void shouldCreateServer() {
        String serverId = transfer.createServer(b -> {}).serverId();

        assertThat(serverId).isNotNull();
    }

}

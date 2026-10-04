package io.floci.testcontainers.gcp.services;

import com.google.api.MonitoredResource;
import com.google.cloud.logging.v2.LoggingClient;
import com.google.cloud.logging.v2.LoggingSettings;
import com.google.logging.v2.ListLogEntriesRequest;
import com.google.logging.v2.LogEntry;
import com.google.logging.v2.WriteLogEntriesRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingServiceTest extends AbstractServiceTest {

    @Test
    void shouldWriteAndListLogEntries() throws Exception {
        String logName = "projects/" + projectId() + "/logs/" + uniqueName("log");

        try (LoggingClient client = LoggingClient.create(LoggingSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.writeLogEntries(WriteLogEntriesRequest.newBuilder()
                    .addEntries(LogEntry.newBuilder()
                            .setLogName(logName)
                            .setResource(MonitoredResource.newBuilder().setType("global"))
                            .setTextPayload("hello"))
                    .build());

            List<LogEntry> entries = new ArrayList<>();
            client.listLogEntries(ListLogEntriesRequest.newBuilder()
                            .addResourceNames("projects/" + projectId())
                            .setFilter("logName=\"" + logName + "\"")
                            .build())
                    .iterateAll().forEach(entries::add);

            assertThat(entries).singleElement()
                    .satisfies(entry -> assertThat(entry.getTextPayload()).isEqualTo("hello"));
        }
    }
}

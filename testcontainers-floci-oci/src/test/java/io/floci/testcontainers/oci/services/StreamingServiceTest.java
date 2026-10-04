package io.floci.testcontainers.oci.services;

import com.oracle.bmc.streaming.StreamAdminClient;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.CreateCursorDetails;
import com.oracle.bmc.streaming.model.CreateStreamDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.model.Stream;
import com.oracle.bmc.streaming.requests.CreateCursorRequest;
import com.oracle.bmc.streaming.requests.CreateStreamRequest;
import com.oracle.bmc.streaming.requests.GetMessagesRequest;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StreamingServiceTest extends AbstractServiceTest {

    private static StreamAdminClient admin;
    private static StreamClient data;

    @BeforeAll
    static void createClients() {
        admin = client(StreamAdminClient.builder());
        // the data plane is served on the same endpoint (the stream's messagesEndpoint is Floci's own base URL)
        data = client(StreamClient.builder());
    }

    @AfterAll
    static void closeClients() {
        admin.close();
        data.close();
    }

    @Test
    void shouldProduceAndConsumeMessages() {
        Stream stream = admin.createStream(CreateStreamRequest.builder()
                .createStreamDetails(CreateStreamDetails.builder()
                        .name("stream-" + System.nanoTime())
                        .partitions(1)
                        .compartmentId(tenancyId())
                        .build())
                .build()).getStream();

        data.putMessages(PutMessagesRequest.builder()
                .streamId(stream.getId())
                .putMessagesDetails(PutMessagesDetails.builder()
                        .messages(List.of(
                                PutMessagesDetailsEntry.builder().value(bytes("first")).build(),
                                PutMessagesDetailsEntry.builder().value(bytes("second")).build()))
                        .build())
                .build());
        String cursor = data.createCursor(CreateCursorRequest.builder()
                .streamId(stream.getId())
                .createCursorDetails(CreateCursorDetails.builder()
                        .partition("0")
                        .type(CreateCursorDetails.Type.TrimHorizon)
                        .build())
                .build()).getCursor().getValue();
        var messages = data.getMessages(GetMessagesRequest.builder()
                .streamId(stream.getId())
                .cursor(cursor)
                .limit(10)
                .build()).getItems();

        assertThat(messages)
                .extracting(m -> new String(m.getValue(), StandardCharsets.UTF_8))
                .containsExactly("first", "second");
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}

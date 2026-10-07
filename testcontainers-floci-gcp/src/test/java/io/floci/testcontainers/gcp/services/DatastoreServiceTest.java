package io.floci.testcontainers.gcp.services;

import com.google.cloud.NoCredentials;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatastoreServiceTest extends AbstractServiceTest {

    @Test
    void shouldPutAndGetEntity() {
        Datastore datastore = DatastoreOptions.newBuilder()
                .setProjectId(projectId())
                .setHost(floci.getEndpoint())
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
        Key key = datastore.newKeyFactory().setKind(uniqueName("Task")).newKey("task-1");

        datastore.put(Entity.newBuilder(key).set("description", "write tests").build());
        Entity entity = datastore.get(key);

        assertThat(entity).isNotNull();
        assertThat(entity.getString("description")).isEqualTo("write tests");
    }
}

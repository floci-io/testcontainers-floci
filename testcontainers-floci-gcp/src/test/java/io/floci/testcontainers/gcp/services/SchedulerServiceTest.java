package io.floci.testcontainers.gcp.services;

import com.google.cloud.scheduler.v1.CloudSchedulerClient;
import com.google.cloud.scheduler.v1.CloudSchedulerSettings;
import com.google.cloud.scheduler.v1.HttpTarget;
import com.google.cloud.scheduler.v1.Job;
import com.google.cloud.scheduler.v1.JobName;
import com.google.cloud.scheduler.v1.LocationName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SchedulerServiceTest extends AbstractServiceTest {

    @Test
    void shouldCreateJob() throws Exception {
        JobName jobName = JobName.of(projectId(), "us-central1", uniqueName("job"));

        try (CloudSchedulerClient client = CloudSchedulerClient.create(CloudSchedulerSettings.newBuilder()
                .setTransportChannelProvider(grpcChannelProvider())
                .setCredentialsProvider(noCredentials())
                .build())) {
            client.createJob(LocationName.of(projectId(), "us-central1"), Job.newBuilder()
                    .setName(jobName.toString())
                    .setSchedule("*/5 * * * *")
                    .setTimeZone("UTC")
                    .setHttpTarget(HttpTarget.newBuilder().setUri("http://localhost:1/unreachable"))
                    .build());

            Job job = client.getJob(jobName);

            assertThat(job.getSchedule()).isEqualTo("*/5 * * * *");
            assertThat(job.getState()).isEqualTo(Job.State.ENABLED);
        }
    }
}

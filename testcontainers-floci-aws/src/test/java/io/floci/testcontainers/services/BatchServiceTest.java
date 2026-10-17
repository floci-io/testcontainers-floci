package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class BatchServiceTest extends AbstractServiceTest {

    static BatchClient batch;
    static String jobQueueArn;
    static String computeEnvArn;

    private static final String COMPUTE_ENV_NAME = "test-compute-env-" + System.currentTimeMillis();
    private static final String JOB_QUEUE_NAME = "test-job-queue-" + System.currentTimeMillis();
    private static final String JOB_DEFINITION_NAME = "test-job-def-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        batch = client(BatchClient.builder());
    }

    @Test
    @Order(1)
    void shouldListComputeEnvironments() {
        List<ComputeEnvironmentDetail> envs = batch.describeComputeEnvironments().computeEnvironments();

        assertThat(envs).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateComputeEnvironment() {
        var response = batch.createComputeEnvironment(b -> b
                .computeEnvironmentName(COMPUTE_ENV_NAME)
                .type(CEType.MANAGED)
                .state(CEState.ENABLED)
                .computeResources(ComputeResource.builder()
                        .type(CRType.EC2)
                        .minvCpus(0)
                        .maxvCpus(256)
                        .instanceTypes("optimal")
                        .subnets("subnet-12345")
                        .securityGroupIds("sg-12345")
                        .instanceRole("arn:aws:iam::000000000000:instance-profile/ecsInstanceRole")
                        .build()));

        computeEnvArn = response.computeEnvironmentArn();
        assertThat(computeEnvArn).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldCreateJobQueue() {
        jobQueueArn = batch.createJobQueue(b -> b
                        .jobQueueName(JOB_QUEUE_NAME)
                        .state(JQState.ENABLED)
                        .priority(1)
                        .computeEnvironmentOrder(ComputeEnvironmentOrder.builder()
                                .order(1)
                                .computeEnvironment(computeEnvArn)
                                .build()))
                .jobQueueArn();

        assertThat(jobQueueArn).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldListJobQueues() {
        List<JobQueueDetail> queues = batch.describeJobQueues().jobQueues();

        assertThat(queues).isNotEmpty();
        assertThat(queues).anyMatch(q -> q.jobQueueName().equals(JOB_QUEUE_NAME));
    }

    @Test
    @Order(5)
    void shouldRegisterJobDefinition() {
        var response = batch.registerJobDefinition(b -> b
                .jobDefinitionName(JOB_DEFINITION_NAME)
                .type(JobDefinitionType.CONTAINER)
                .containerProperties(ContainerProperties.builder()
                        .image("amazonlinux:2")
                        .vcpus(1)
                        .memory(512)
                        .command("echo", "hello from floci batch")
                        .build()));

        assertThat(response.jobDefinitionArn()).isNotBlank();
    }

    @Test
    @Order(6)
    void shouldSubmitJob() {
        String jobId = batch.submitJob(b -> b
                        .jobName("test-job-" + System.currentTimeMillis())
                        .jobQueue(jobQueueArn)
                        .jobDefinition(JOB_DEFINITION_NAME))
                .jobId();

        assertThat(jobId).isNotBlank();
    }

    @Test
    @Order(7)
    void shouldListJobs() {
        List<JobSummary> jobs = batch.listJobs(b -> b.jobQueue(jobQueueArn)).jobSummaryList();

        assertThat(jobs).isNotNull();
    }

    @Test
    @Order(8)
    void shouldDeregisterJobDefinition() {
        String jobDefArn = JOB_DEFINITION_NAME + ":1";

        batch.deregisterJobDefinition(b -> b.jobDefinition(jobDefArn));

        List<JobDefinition> defs = batch.describeJobDefinitions(b -> b
                        .jobDefinitionName(JOB_DEFINITION_NAME)
                        .status("INACTIVE"))
                .jobDefinitions();

        assertThat(defs).anyMatch(d -> d.jobDefinitionName().equals(JOB_DEFINITION_NAME));
    }
}

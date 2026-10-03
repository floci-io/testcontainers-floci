package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.EndpointConfigSummary;
import software.amazon.awssdk.services.sagemaker.model.TrainingJobStatus;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class SageMakerServiceTest extends AbstractServiceTest {

    private static final String TRAINING_IMAGE = "busybox:stable";
    private static final String ROLE_ARN = "arn:aws:iam::000000000000:role/sagemaker-role";
    private static final String MODEL_NAME = "floci-tc-model";
    private static final String ENDPOINT_CONFIG_NAME = "floci-tc-endpoint-config";
    private static final String TRAINING_JOB_NAME = "floci-tc-training-job";
    private static final String BUCKET = "floci-tc-sagemaker";

    static SageMakerClient sageMaker;
    static S3Client s3;

    @BeforeAll
    static void setUp() {
        sageMaker = client(SageMakerClient.builder());
        s3 = client(S3Client.builder().forcePathStyle(true));
    }

    @Test
    @Order(1)
    void shouldCreateAndDescribeModel() {
        var response = sageMaker.createModel(b -> b
                .modelName(MODEL_NAME)
                .primaryContainer(c -> c.image(TRAINING_IMAGE))
                .executionRoleArn(ROLE_ARN));

        assertThat(response.modelArn()).contains(":model/" + MODEL_NAME);
        assertThat(sageMaker.describeModel(b -> b.modelName(MODEL_NAME)).modelName()).isEqualTo(MODEL_NAME);
    }

    @Test
    @Order(2)
    void shouldCreateAndListEndpointConfig() {
        sageMaker.createEndpointConfig(b -> b
                .endpointConfigName(ENDPOINT_CONFIG_NAME)
                .productionVariants(v -> v
                        .variantName("AllTraffic")
                        .modelName(MODEL_NAME)
                        .initialInstanceCount(1)
                        .instanceType("ml.t2.medium")));

        assertThat(sageMaker.listEndpointConfigs(b -> {}).endpointConfigs())
                .extracting(EndpointConfigSummary::endpointConfigName)
                .contains(ENDPOINT_CONFIG_NAME);
    }

    @Test
    @Order(3)
    void shouldRunTrainingJobToCompletion() {
        s3.createBucket(b -> b.bucket(BUCKET));
        s3.putObject(b -> b.bucket(BUCKET).key("input/data.txt"), RequestBody.fromString("hello"));

        sageMaker.createTrainingJob(b -> b
                .trainingJobName(TRAINING_JOB_NAME)
                .roleArn(ROLE_ARN)
                .algorithmSpecification(a -> a
                        .trainingImage(TRAINING_IMAGE)
                        .trainingInputMode("File")
                        .containerEntrypoint("/bin/sh", "-c")
                        .containerArguments("mkdir -p /opt/ml/model && echo ok > /opt/ml/model/model.txt"))
                .inputDataConfig(c -> c
                        .channelName("train")
                        .dataSource(d -> d.s3DataSource(s -> s
                                .s3DataType("S3Prefix")
                                .s3Uri("s3://" + BUCKET + "/input"))))
                .outputDataConfig(o -> o.s3OutputPath("s3://" + BUCKET + "/output"))
                .resourceConfig(c -> c
                        .instanceType("ml.m5.large")
                        .instanceCount(1)
                        .volumeSizeInGB(1))
                .stoppingCondition(c -> c.maxRuntimeInSeconds(60)));

        await().atMost(Duration.ofSeconds(120)).pollInterval(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThat(sageMaker.describeTrainingJob(b -> b.trainingJobName(TRAINING_JOB_NAME)).trainingJobStatus())
                        .isEqualTo(TrainingJobStatus.COMPLETED));

        String artifact = sageMaker.describeTrainingJob(b -> b.trainingJobName(TRAINING_JOB_NAME))
                .modelArtifacts().s3ModelArtifacts();
        String key = artifact.substring(("s3://" + BUCKET + "/").length());
        assertThat(s3.getObjectAsBytes(b -> b.bucket(BUCKET).key(key)).asByteArray()).isNotEmpty();
    }

    @Test
    @Order(4)
    void shouldDeleteEndpointConfigAndModel() {
        sageMaker.deleteEndpointConfig(b -> b.endpointConfigName(ENDPOINT_CONFIG_NAME));
        sageMaker.deleteModel(b -> b.modelName(MODEL_NAME));

        assertThat(sageMaker.listEndpointConfigs(b -> {}).endpointConfigs())
                .extracting(EndpointConfigSummary::endpointConfigName)
                .doesNotContain(ENDPOINT_CONFIG_NAME);
    }
}

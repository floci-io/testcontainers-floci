package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.codepipeline.CodePipelineClient;
import software.amazon.awssdk.services.codepipeline.model.ActionCategory;
import software.amazon.awssdk.services.codepipeline.model.ActionOwner;
import software.amazon.awssdk.services.codepipeline.model.ActionTypeId;
import software.amazon.awssdk.services.codepipeline.model.ArtifactStore;
import software.amazon.awssdk.services.codepipeline.model.ArtifactStoreType;
import software.amazon.awssdk.services.codepipeline.model.PipelineSummary;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class CodePipelineServiceTest extends AbstractServiceTest {

    static CodePipelineClient codePipeline;

    private static final String PIPELINE_NAME = "test-pipeline-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        codePipeline = client(CodePipelineClient.builder());
    }

    @Test
    @Order(1)
    void shouldListPipelines() {
        List<PipelineSummary> pipelines = codePipeline.listPipelines().pipelines();

        assertThat(pipelines).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreatePipeline() {
        var response = codePipeline.createPipeline(b -> b.pipeline(p -> p
                .name(PIPELINE_NAME)
                .roleArn("arn:aws:iam::000000000000:role/test-role")
                .artifactStore(ArtifactStore.builder()
                        .type(ArtifactStoreType.S3)
                        .location("test-pipeline-artifacts")
                        .build())
                .stages(
                        stage -> stage
                                .name("Source")
                                .actions(action -> action
                                        .name("SourceAction")
                                        .actionTypeId(ActionTypeId.builder()
                                                .category(ActionCategory.SOURCE)
                                                .owner(ActionOwner.AWS)
                                                .provider("S3")
                                                .version("1")
                                                .build())
                                        .configuration(Map.of(
                                                "S3Bucket", "test-pipeline-source",
                                                "S3ObjectKey", "source.zip"))
                                        .outputArtifacts(o -> o.name("SourceOutput"))),
                        stage -> stage
                                .name("Approval")
                                .actions(action -> action
                                        .name("ApprovalAction")
                                        .actionTypeId(ActionTypeId.builder()
                                                .category(ActionCategory.APPROVAL)
                                                .owner(ActionOwner.AWS)
                                                .provider("Manual")
                                                .version("1")
                                                .build())))));

        assertThat(response.pipeline().name()).isEqualTo(PIPELINE_NAME);
    }

    @Test
    @Order(3)
    void shouldListPipelinesContainsCreatedPipeline() {
        List<PipelineSummary> pipelines = codePipeline.listPipelines().pipelines();

        assertThat(pipelines).anyMatch(p -> p.name().equals(PIPELINE_NAME));
    }

    @Test
    @Order(4)
    void shouldDeletePipeline() {
        codePipeline.deletePipeline(b -> b.name(PIPELINE_NAME));

        List<PipelineSummary> pipelines = codePipeline.listPipelines().pipelines();
        assertThat(pipelines).noneMatch(p -> p.name().equals(PIPELINE_NAME));
    }
}

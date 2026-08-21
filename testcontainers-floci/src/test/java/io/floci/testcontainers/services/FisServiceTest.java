package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.fis.FisClient;
import software.amazon.awssdk.services.fis.model.CreateExperimentTemplateActionInput;
import software.amazon.awssdk.services.fis.model.CreateExperimentTemplateTargetInput;
import software.amazon.awssdk.services.fis.model.ExperimentTemplateSummary;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class FisServiceTest extends AbstractServiceTest {

    static FisClient fis;

    static String experimentTemplateId;

    @BeforeAll
    static void setUp() {
        fis = client(FisClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateExperimentTemplate() {
        var response = fis.createExperimentTemplate(b -> b
                .description("test experiment template")
                .roleArn("arn:aws:iam::000000000000:role/fis-role")
                .stopConditions(s -> s.source("none"))
                .targets(Map.of("Instances-Target-1", CreateExperimentTemplateTargetInput.builder()
                        .resourceType("aws:ec2:instance")
                        .resourceTags(Map.of("Name", "test-instance"))
                        .selectionMode("ALL")
                        .build()))
                .actions(Map.of("StopInstances", CreateExperimentTemplateActionInput.builder()
                        .actionId("aws:ec2:stop-instances")
                        .targets(Map.of("Instances", "Instances-Target-1"))
                        .build())));

        assertThat(response.experimentTemplate().id()).isNotBlank();
        experimentTemplateId = response.experimentTemplate().id();
    }

    @Test
    @Order(2)
    void shouldGetExperimentTemplate() {
        var response = fis.getExperimentTemplate(b -> b.id(experimentTemplateId));

        assertThat(response.experimentTemplate().id()).isEqualTo(experimentTemplateId);
        assertThat(response.experimentTemplate().description()).isEqualTo("test experiment template");
    }

    @Test
    @Order(3)
    void shouldListExperimentTemplatesContainsCreatedTemplate() {
        var response = fis.listExperimentTemplates(b -> {});

        assertThat(response.experimentTemplates())
                .extracting(ExperimentTemplateSummary::id)
                .contains(experimentTemplateId);
    }

    @Test
    @Order(4)
    void shouldUpdateExperimentTemplate() {
        fis.updateExperimentTemplate(b -> b
                .id(experimentTemplateId)
                .description("updated experiment template"));

        var response = fis.getExperimentTemplate(b -> b.id(experimentTemplateId));
        assertThat(response.experimentTemplate().description()).isEqualTo("updated experiment template");
    }

    @Test
    @Order(5)
    void shouldDeleteExperimentTemplate() {
        fis.deleteExperimentTemplate(b -> b.id(experimentTemplateId));

        var response = fis.listExperimentTemplates(b -> {});
        assertThat(response.experimentTemplates())
                .extracting(ExperimentTemplateSummary::id)
                .doesNotContain(experimentTemplateId);
    }
}

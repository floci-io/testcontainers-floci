package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.codebuild.CodeBuildClient;
import software.amazon.awssdk.services.codebuild.model.ProjectSortByType;
import software.amazon.awssdk.services.codebuild.model.SortOrderType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeBuildServiceTest extends AbstractServiceTest {

    static CodeBuildClient codeBuild;

    @BeforeAll
    static void setUp() {
        codeBuild = client(CodeBuildClient.builder());
    }

    @Test
    void shouldListProjects() {
        List<String> projects = codeBuild.listProjects(b -> b
                .sortBy(ProjectSortByType.NAME)
                .sortOrder(SortOrderType.ASCENDING)).projects();

        assertThat(projects).isNotNull();
    }

}

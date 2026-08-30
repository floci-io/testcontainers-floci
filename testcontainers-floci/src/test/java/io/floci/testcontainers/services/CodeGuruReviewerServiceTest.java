package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.codegurureviewer.CodeGuruReviewerClient;
import software.amazon.awssdk.services.codegurureviewer.model.ProviderType;
import software.amazon.awssdk.services.codegurureviewer.model.RepositoryAssociationSummary;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
@Disabled("CodeGuru Reviewer is not yet registered in the floci/floci:nightly image")
class CodeGuruReviewerServiceTest extends AbstractServiceTest {

    static CodeGuruReviewerClient codeGuruReviewer;

    static String associationArn;
    static String associationId;

    @BeforeAll
    static void setUp() {
        codeGuruReviewer = client(CodeGuruReviewerClient.builder());
    }

    @Test
    @Order(1)
    void shouldAssociateRepository() {
        var response = codeGuruReviewer.associateRepository(b -> b
                .repository(r -> r.codeCommit(c -> c.name("floci-service"))));

        assertThat(response.repositoryAssociation().associationId()).isNotBlank();
        assertThat(response.repositoryAssociation().name()).isEqualTo("floci-service");
        assertThat(response.repositoryAssociation().providerType()).isEqualTo(ProviderType.CODE_COMMIT);
        associationArn = response.repositoryAssociation().associationArn();
        associationId = response.repositoryAssociation().associationId();
    }

    @Test
    @Order(2)
    void shouldDescribeRepositoryAssociation() {
        var response = codeGuruReviewer.describeRepositoryAssociation(b -> b.associationArn(associationArn));

        assertThat(response.repositoryAssociation().associationId()).isEqualTo(associationId);
        assertThat(response.repositoryAssociation().name()).isEqualTo("floci-service");
    }

    @Test
    @Order(3)
    void shouldListRepositoryAssociationsContainsCreatedAssociation() {
        var response = codeGuruReviewer.listRepositoryAssociations(b -> {});

        assertThat(response.repositoryAssociationSummaries())
                .extracting(RepositoryAssociationSummary::associationId)
                .contains(associationId);
    }

    @Test
    @Order(4)
    void shouldDisassociateRepository() {
        codeGuruReviewer.disassociateRepository(b -> b.associationArn(associationArn));

        var response = codeGuruReviewer.listRepositoryAssociations(b -> {});
        assertThat(response.repositoryAssociationSummaries())
                .extracting(RepositoryAssociationSummary::associationId)
                .doesNotContain(associationId);
    }
}

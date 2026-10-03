package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.codeartifact.CodeartifactClient;
import software.amazon.awssdk.services.codeartifact.model.DomainStatus;
import software.amazon.awssdk.services.codeartifact.model.PackageFormat;
import software.amazon.awssdk.services.codeartifact.model.RepositorySummary;
import software.amazon.awssdk.services.codeartifact.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class CodeArtifactServiceTest extends AbstractServiceTest {

    private static final String DOMAIN = "floci-tc-domain";
    private static final String REPOSITORY = "floci-tc-repository";

    static CodeartifactClient codeArtifact;

    @BeforeAll
    static void setUp() {
        codeArtifact = client(CodeartifactClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateDomain() {
        var domain = codeArtifact.createDomain(b -> b.domain(DOMAIN)).domain();

        assertThat(domain.name()).isEqualTo(DOMAIN);
        assertThat(domain.status()).isEqualTo(DomainStatus.ACTIVE);
    }

    @Test
    @Order(2)
    void shouldCreateAndListRepository() {
        var repository = codeArtifact.createRepository(b -> b.domain(DOMAIN).repository(REPOSITORY)).repository();

        assertThat(repository.name()).isEqualTo(REPOSITORY);
        assertThat(codeArtifact.listRepositoriesInDomain(b -> b.domain(DOMAIN)).repositories())
                .extracting(RepositorySummary::name)
                .contains(REPOSITORY);
    }

    @Test
    @Order(3)
    void shouldGetAuthorizationTokenAndRepositoryEndpoint() {
        assertThat(codeArtifact.getAuthorizationToken(b -> b.domain(DOMAIN)).authorizationToken()).isNotBlank();
        assertThat(codeArtifact.getRepositoryEndpoint(b -> b
                .domain(DOMAIN)
                .repository(REPOSITORY)
                .format(PackageFormat.MAVEN)).repositoryEndpoint()).isNotBlank();
    }

    @Test
    @Order(4)
    void shouldDeleteRepositoryAndDomain() {
        codeArtifact.deleteRepository(b -> b.domain(DOMAIN).repository(REPOSITORY));
        codeArtifact.deleteDomain(b -> b.domain(DOMAIN));

        assertThatThrownBy(() -> codeArtifact.describeDomain(b -> b.domain(DOMAIN)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

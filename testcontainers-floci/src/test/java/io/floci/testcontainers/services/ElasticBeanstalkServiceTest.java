package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.ApplicationDescription;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ElasticBeanstalkServiceTest extends AbstractServiceTest {

    static ElasticBeanstalkClient elasticBeanstalk;

    private static final String APPLICATION_NAME = "test-application-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        elasticBeanstalk = client(ElasticBeanstalkClient.builder());
    }

    @Test
    @Order(1)
    void shouldDescribeApplications() {
        List<ApplicationDescription> applications = elasticBeanstalk.describeApplications().applications();

        assertThat(applications).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateApplication() {
        var response = elasticBeanstalk.createApplication(b -> b
                .applicationName(APPLICATION_NAME)
                .description("Test application"));

        assertThat(response.application().applicationName()).isEqualTo(APPLICATION_NAME);
    }

    @Test
    @Order(3)
    void shouldDescribeApplicationsContainsCreatedApplication() {
        List<ApplicationDescription> applications = elasticBeanstalk.describeApplications(
                b -> b.applicationNames(APPLICATION_NAME)).applications();

        assertThat(applications).anyMatch(a -> a.applicationName().equals(APPLICATION_NAME));
    }

    @Test
    @Order(4)
    void shouldDeleteApplication() {
        elasticBeanstalk.deleteApplication(b -> b.applicationName(APPLICATION_NAME));

        List<ApplicationDescription> applications = elasticBeanstalk.describeApplications().applications();
        assertThat(applications).noneMatch(a -> a.applicationName().equals(APPLICATION_NAME));
    }
}

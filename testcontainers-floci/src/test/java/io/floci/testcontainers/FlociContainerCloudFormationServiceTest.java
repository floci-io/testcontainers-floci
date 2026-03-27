package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.StackSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerCloudFormationServiceTest extends AbstractFlociContainerServiceTest {

    static CloudFormationClient cfn;

    private static final String TEMPLATE = """
            {
              "AWSTemplateFormatVersion": "2010-09-09",
              "Description": "Test stack",
              "Resources": {
                "TestBucket": {
                  "Type": "AWS::S3::Bucket",
                  "Properties": {
                    "BucketName": "cfn-test-%s"
                  }
                }
              }
            }
            """;

    @BeforeAll
    static void setUp() {
        cfn = client(CloudFormationClient.builder());
    }

    @Test
    void shouldCreateAndListStack() {
        String stackName = "test-stack-" + System.currentTimeMillis();
        String template = TEMPLATE.formatted(stackName);

        cfn.createStack(b -> b.stackName(stackName).templateBody(template));

        List<String> stackNames = cfn.listStacks().stackSummaries().stream()
                .map(StackSummary::stackName)
                .toList();

        assertThat(stackNames).contains(stackName);
    }

    @Test
    void shouldDescribeStack() {
        String stackName = "test-describe-" + System.currentTimeMillis();
        String template = TEMPLATE.formatted(stackName);

        cfn.createStack(b -> b.stackName(stackName).templateBody(template));

        String description = cfn.describeStacks(b -> b.stackName(stackName))
                .stacks().get(0).stackName();

        assertThat(description).isEqualTo(stackName);
    }

}

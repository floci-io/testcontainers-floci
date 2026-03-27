package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static software.amazon.awssdk.regions.Region.AWS_GLOBAL;

class FlociContainerIamServiceTest extends AbstractFlociContainerServiceTest {

    static IamClient iam;

    @BeforeAll
    static void setUp() {
        iam = client(IamClient.builder().region(AWS_GLOBAL));
    }

    @Test
    void shouldCreateAndListUser() {
        String userName = "test-user-" + System.currentTimeMillis();

        iam.createUser(b -> b.userName(userName));

        List<String> userNames = iam.listUsers().users().stream()
                .map(User::userName)
                .toList();

        assertThat(userNames).contains(userName);
    }

    @Test
    void shouldCreateAndGetRole() {
        String roleName = "test-role-" + System.currentTimeMillis();
        String assumeRolePolicy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [{
                    "Effect": "Allow",
                    "Principal": {"Service": "lambda.amazonaws.com"},
                    "Action": "sts:AssumeRole"
                  }]
                }
                """;

        iam.createRole(b -> b.roleName(roleName).assumeRolePolicyDocument(assumeRolePolicy));

        String retrievedArn = iam.getRole(b -> b.roleName(roleName)).role().arn();

        assertThat(retrievedArn).contains(roleName);
    }

}

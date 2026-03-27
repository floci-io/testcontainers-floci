package io.github.hectorvent.testcontainers.floci;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerCognitoServiceTest extends AbstractFlociContainerServiceTest {

    static CognitoIdentityProviderClient cognito;

    @BeforeAll
    static void setUp() {
        cognito = client(CognitoIdentityProviderClient.builder());
    }

    @Test
    void shouldCreateAndListUserPool() {
        String poolName = "test-pool-" + System.currentTimeMillis();

        String userPoolId = cognito.createUserPool(b -> b.poolName(poolName)).userPool().id();

        assertThat(userPoolId).isNotBlank();

        List<String> poolNames = cognito.listUserPools(b -> b.maxResults(50)).userPools().stream()
                .map(UserPoolDescriptionType::name)
                .toList();

        assertThat(poolNames).contains(poolName);
    }

    @Test
    void shouldDescribeUserPool() {
        String poolName = "test-describe-" + System.currentTimeMillis();
        String userPoolId = cognito.createUserPool(b -> b.poolName(poolName)).userPool().id();

        String name = cognito.describeUserPool(b -> b.userPoolId(userPoolId))
                .userPool()
                .name();

        assertThat(name).isEqualTo(poolName);
    }

}

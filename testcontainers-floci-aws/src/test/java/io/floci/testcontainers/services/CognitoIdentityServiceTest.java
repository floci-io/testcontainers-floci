package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityPoolShortDescription;
import software.amazon.awssdk.services.cognitoidentity.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class CognitoIdentityServiceTest extends AbstractServiceTest {

    static CognitoIdentityClient cognitoIdentity;

    static String identityPoolId;

    @BeforeAll
    static void setUp() {
        cognitoIdentity = client(CognitoIdentityClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateIdentityPool() {
        var response = cognitoIdentity.createIdentityPool(b -> b
                .identityPoolName("floci_tc_identity_pool")
                .allowUnauthenticatedIdentities(true));

        assertThat(response.identityPoolId()).startsWith(floci.getRegion() + ":");
        identityPoolId = response.identityPoolId();
    }

    @Test
    @Order(2)
    void shouldDescribeAndListIdentityPool() {
        assertThat(cognitoIdentity.describeIdentityPool(b -> b.identityPoolId(identityPoolId)).identityPoolName())
                .isEqualTo("floci_tc_identity_pool");
        assertThat(cognitoIdentity.listIdentityPools(b -> b.maxResults(60)).identityPools())
                .extracting(IdentityPoolShortDescription::identityPoolId)
                .contains(identityPoolId);
    }

    @Test
    @Order(3)
    void shouldSetIdentityPoolRoles() {
        cognitoIdentity.setIdentityPoolRoles(b -> b
                .identityPoolId(identityPoolId)
                .roles(java.util.Map.of("unauthenticated", "arn:aws:iam::000000000000:role/unauth")));

        assertThat(cognitoIdentity.getIdentityPoolRoles(b -> b.identityPoolId(identityPoolId)).roles())
                .containsEntry("unauthenticated", "arn:aws:iam::000000000000:role/unauth");
    }

    @Test
    @Order(4)
    void shouldDeleteIdentityPool() {
        cognitoIdentity.deleteIdentityPool(b -> b.identityPoolId(identityPoolId));

        assertThatThrownBy(() -> cognitoIdentity.describeIdentityPool(b -> b.identityPoolId(identityPoolId)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

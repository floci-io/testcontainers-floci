package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.verifiedpermissions.VerifiedPermissionsClient;
import software.amazon.awssdk.services.verifiedpermissions.model.Decision;
import software.amazon.awssdk.services.verifiedpermissions.model.PolicyItem;
import software.amazon.awssdk.services.verifiedpermissions.model.ValidationMode;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class VerifiedPermissionsServiceTest extends AbstractServiceTest {

    static VerifiedPermissionsClient verifiedPermissions;

    static String policyStoreId;
    static String policyId;

    @BeforeAll
    static void setUp() {
        verifiedPermissions = client(VerifiedPermissionsClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreatePolicyStore() {
        policyStoreId = verifiedPermissions.createPolicyStore(b -> b
                .validationSettings(v -> v.mode(ValidationMode.OFF))).policyStoreId();

        assertThat(verifiedPermissions.getPolicyStore(b -> b.policyStoreId(policyStoreId)).policyStoreId())
                .isEqualTo(policyStoreId);
    }

    @Test
    @Order(2)
    void shouldCreatePolicy() {
        policyId = verifiedPermissions.createPolicy(b -> b
                .policyStoreId(policyStoreId)
                .definition(d -> d.staticValue(s -> s.statement(
                        "permit(principal == User::\"alice\", action == Action::\"read\", resource);"))))
                .policyId();

        assertThat(verifiedPermissions.listPolicies(b -> b.policyStoreId(policyStoreId)).policies())
                .extracting(PolicyItem::policyId)
                .contains(policyId);
    }

    @Test
    @Order(3)
    void shouldAuthorizeWithCedar() {
        var allowed = verifiedPermissions.isAuthorized(b -> b
                .policyStoreId(policyStoreId)
                .principal(p -> p.entityType("User").entityId("alice"))
                .action(a -> a.actionType("Action").actionId("read"))
                .resource(r -> r.entityType("Document").entityId("doc1")));
        var denied = verifiedPermissions.isAuthorized(b -> b
                .policyStoreId(policyStoreId)
                .principal(p -> p.entityType("User").entityId("bob"))
                .action(a -> a.actionType("Action").actionId("read"))
                .resource(r -> r.entityType("Document").entityId("doc1")));

        assertThat(allowed.decision()).isEqualTo(Decision.ALLOW);
        assertThat(denied.decision()).isEqualTo(Decision.DENY);
    }

    @Test
    @Order(4)
    void shouldDeletePolicyStore() {
        verifiedPermissions.deletePolicyStore(b -> b.policyStoreId(policyStoreId));

        assertThat(verifiedPermissions.listPolicyStores(b -> {}).policyStores())
                .noneMatch(store -> store.policyStoreId().equals(policyStoreId));
    }
}

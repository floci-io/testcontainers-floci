package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.Email;
import software.amazon.awssdk.services.identitystore.model.GroupMembership;
import software.amazon.awssdk.services.identitystore.model.ResourceNotFoundException;
import software.amazon.awssdk.services.identitystore.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(OrderAnnotation.class)
class IdentityStoreServiceTest extends AbstractServiceTest {

    private static final String IDENTITY_STORE_ID = "d-1234567890";

    static IdentitystoreClient identityStore;

    static String groupId;
    static String userId;

    @BeforeAll
    static void setUp() {
        identityStore = client(IdentitystoreClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateGroupAndUser() {
        groupId = identityStore.createGroup(b -> b
                .identityStoreId(IDENTITY_STORE_ID)
                .displayName("PlatformAdmins")).groupId();
        userId = identityStore.createUser(b -> b
                .identityStoreId(IDENTITY_STORE_ID)
                .userName("jane@example.com")
                .displayName("Jane Doe")
                .emails(Email.builder().value("jane@example.com").primary(true).build())).userId();

        assertThat(groupId).isNotBlank();
        assertThat(userId).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldDescribeAndListUser() {
        assertThat(identityStore.describeUser(b -> b.identityStoreId(IDENTITY_STORE_ID).userId(userId)).userName())
                .isEqualTo("jane@example.com");
        assertThat(identityStore.listUsers(b -> b.identityStoreId(IDENTITY_STORE_ID)).users())
                .extracting(User::userId)
                .contains(userId);
    }

    @Test
    @Order(3)
    void shouldAddUserToGroup() {
        identityStore.createGroupMembership(b -> b
                .identityStoreId(IDENTITY_STORE_ID)
                .groupId(groupId)
                .memberId(m -> m.userId(userId)));

        assertThat(identityStore.listGroupMemberships(b -> b.identityStoreId(IDENTITY_STORE_ID).groupId(groupId))
                .groupMemberships())
                .extracting(GroupMembership::memberId)
                .anySatisfy(member -> assertThat(member.userId()).isEqualTo(userId));
    }

    @Test
    @Order(4)
    void shouldDeleteUser() {
        identityStore.deleteUser(b -> b.identityStoreId(IDENTITY_STORE_ID).userId(userId));

        assertThatThrownBy(() -> identityStore.describeUser(b -> b.identityStoreId(IDENTITY_STORE_ID).userId(userId)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

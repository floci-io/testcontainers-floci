package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.macie2.Macie2Client;
import software.amazon.awssdk.services.macie2.model.AdminAccount;
import software.amazon.awssdk.services.macie2.model.MacieStatus;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class Macie2ServiceTest extends AbstractServiceTest {

    private static final String ADMIN_ACCOUNT = "111111111111";

    static Macie2Client macie;

    @BeforeAll
    static void setUp() {
        macie = client(Macie2Client.builder());
    }

    @Test
    @Order(1)
    void shouldEnableMacie() {
        macie.enableMacie(b -> {});

        assertThat(macie.getMacieSession(b -> {}).status()).isEqualTo(MacieStatus.ENABLED);
    }

    @Test
    @Order(2)
    void shouldEnableOrganizationAdminAccount() {
        macie.enableOrganizationAdminAccount(b -> b.adminAccountId(ADMIN_ACCOUNT));

        assertThat(macie.listOrganizationAdminAccounts(b -> {}).adminAccounts())
                .extracting(AdminAccount::accountId)
                .contains(ADMIN_ACCOUNT);
    }
}

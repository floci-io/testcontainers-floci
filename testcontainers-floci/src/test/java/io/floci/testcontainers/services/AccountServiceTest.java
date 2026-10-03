package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.account.AccountClient;
import software.amazon.awssdk.services.account.model.AlternateContactType;

import static org.assertj.core.api.Assertions.assertThat;

class AccountServiceTest extends AbstractServiceTest {

    static AccountClient account;

    @BeforeAll
    static void setUp() {
        account = client(AccountClient.builder());
    }

    @Test
    void shouldPutAndGetAlternateContact() {
        account.putAlternateContact(b -> b
                .alternateContactType(AlternateContactType.SECURITY)
                .emailAddress("security@example.com")
                .name("Security Team")
                .phoneNumber("+1 555 0100")
                .title("Security"));

        var contact = account.getAlternateContact(b -> b.alternateContactType(AlternateContactType.SECURITY))
                .alternateContact();
        assertThat(contact.emailAddress()).isEqualTo("security@example.com");
        assertThat(contact.name()).isEqualTo("Security Team");
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.ram.RamClient;
import software.amazon.awssdk.services.ram.model.ResourceOwner;
import software.amazon.awssdk.services.ram.model.ResourceShare;
import software.amazon.awssdk.services.ram.model.ResourceShareStatus;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class RamServiceTest extends AbstractServiceTest {

    private static final String SHARE_NAME = "floci-tc-tgw-share";
    private static final String TGW_ARN = "arn:aws:ec2:us-east-1:000000000000:transit-gateway/tgw-0abc";

    static RamClient ram;

    static String resourceShareArn;

    @BeforeAll
    static void setUp() {
        ram = client(RamClient.builder());
    }

    @Test
    @Order(1)
    void shouldEnableSharingWithAwsOrganization() {
        var response = ram.enableSharingWithAwsOrganization();

        assertThat(response.returnValue()).isTrue();
    }

    @Test
    @Order(2)
    void shouldCreateResourceShare() {
        var response = ram.createResourceShare(b -> b
                .name(SHARE_NAME)
                .resourceArns(TGW_ARN));

        assertThat(response.resourceShare().name()).isEqualTo(SHARE_NAME);
        assertThat(response.resourceShare().status()).isEqualTo(ResourceShareStatus.ACTIVE);
        resourceShareArn = response.resourceShare().resourceShareArn();
    }

    @Test
    @Order(3)
    void shouldGetResourceSharesContainsCreatedShare() {
        var response = ram.getResourceShares(b -> b.resourceOwner(ResourceOwner.SELF));

        assertThat(response.resourceShares())
                .extracting(ResourceShare::name)
                .contains(SHARE_NAME);
    }

    @Test
    @Order(4)
    void shouldDeleteResourceShare() {
        ram.deleteResourceShare(b -> b.resourceShareArn(resourceShareArn));

        var response = ram.getResourceShares(b -> b.resourceOwner(ResourceOwner.SELF));
        assertThat(response.resourceShares())
                .filteredOn(s -> s.resourceShareArn().equals(resourceShareArn))
                .allMatch(s -> s.status() == ResourceShareStatus.DELETED);
    }
}

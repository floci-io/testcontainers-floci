package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.globalaccelerator.GlobalAcceleratorClient;
import software.amazon.awssdk.services.globalaccelerator.model.Accelerator;
import software.amazon.awssdk.services.globalaccelerator.model.IpAddressType;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class GlobalAcceleratorServiceTest extends AbstractServiceTest {

    static GlobalAcceleratorClient globalAccelerator;

    static String acceleratorArn;

    @BeforeAll
    static void setUp() {
        globalAccelerator = client(GlobalAcceleratorClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateAccelerator() {
        Accelerator accelerator = globalAccelerator.createAccelerator(b -> b
                .name("floci-tc-accelerator")
                .ipAddressType(IpAddressType.IPV4)
                .enabled(true)).accelerator();

        assertThat(accelerator.acceleratorArn()).contains(":accelerator/");
        assertThat(accelerator.dnsName()).endsWith(".awsglobalaccelerator.com");
        acceleratorArn = accelerator.acceleratorArn();
    }

    @Test
    @Order(2)
    void shouldDescribeAndListAccelerator() {
        assertThat(globalAccelerator.describeAccelerator(b -> b.acceleratorArn(acceleratorArn)).accelerator().name())
                .isEqualTo("floci-tc-accelerator");
        assertThat(globalAccelerator.listAccelerators(b -> {}).accelerators())
                .extracting(Accelerator::acceleratorArn)
                .contains(acceleratorArn);
    }

    @Test
    @Order(3)
    void shouldDeleteAccelerator() {
        // AWS only deletes disabled accelerators
        globalAccelerator.updateAccelerator(b -> b.acceleratorArn(acceleratorArn).enabled(false));
        globalAccelerator.deleteAccelerator(b -> b.acceleratorArn(acceleratorArn));

        assertThat(globalAccelerator.listAccelerators(b -> {}).accelerators())
                .extracting(Accelerator::acceleratorArn)
                .doesNotContain(acceleratorArn);
    }
}

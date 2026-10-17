package io.floci.testcontainers.oci.services;

import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.CreateCompartmentDetails;
import com.oracle.bmc.identity.requests.CreateCompartmentRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.queue.QueueAdminClient;
import com.oracle.bmc.queue.model.CreateQueueDetails;
import com.oracle.bmc.queue.requests.CreateQueueRequest;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityServiceTest extends AbstractServiceTest {

    private static IdentityClient identity;

    @BeforeAll
    static void createClient() {
        identity = client(IdentityClient.builder());
    }

    @AfterAll
    static void closeClient() {
        identity.close();
    }

    @Test
    void shouldCreateAndListCompartment() {
        Compartment compartment = identity.createCompartment(CreateCompartmentRequest.builder()
                .createCompartmentDetails(CreateCompartmentDetails.builder()
                        .compartmentId(tenancyId())
                        .name("compartment-" + System.nanoTime())
                        .description("created by testcontainers-floci")
                        .build())
                .build()).getCompartment();

        var compartments = identity.listCompartments(ListCompartmentsRequest.builder()
                .compartmentId(tenancyId())
                .build()).getItems();

        assertThat(compartment.getId()).startsWith("ocid1.compartment.");
        assertThat(compartment.getLifecycleState()).isEqualTo(Compartment.LifecycleState.Active);
        assertThat(compartments).extracting(Compartment::getId).contains(compartment.getId());
    }

    @Test
    void shouldRejectUnknownCompartmentWithStrictCompartments() {
        try (FlociOciContainer strict = new FlociOciContainer(NIGHTLY_IMAGE)
                .disableAllServices()
                .withIdentityConfig(c -> c.enabled(true).strictCompartments(true))
                .withQueueConfig(c -> c.enabled(true))) {
            strict.start();
            try (QueueAdminClient admin = client(strict, QueueAdminClient.builder())) {
                CreateQueueRequest request = CreateQueueRequest.builder()
                        .createQueueDetails(CreateQueueDetails.builder()
                                .compartmentId("ocid1.compartment.oc1..unknown")
                                .displayName("queue-" + System.nanoTime())
                                .build())
                        .build();

                assertThatThrownBy(() -> admin.createQueue(request))
                        .isInstanceOfSatisfying(BmcException.class, e -> {
                            assertThat(e.getStatusCode()).isEqualTo(400);
                            assertThat(e.getServiceCode()).isEqualTo("RelatedResourceNotAuthorizedOrNotFound");
                        });
            }
        }
    }
}

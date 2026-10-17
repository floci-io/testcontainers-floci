package io.floci.testcontainers.oci.services;

import com.github.dockerjava.api.DockerClient;
import com.oracle.bmc.functions.FunctionsInvokeClient;
import com.oracle.bmc.functions.FunctionsManagementClient;
import com.oracle.bmc.functions.model.CreateApplicationDetails;
import com.oracle.bmc.functions.model.CreateFunctionDetails;
import com.oracle.bmc.functions.requests.CreateApplicationRequest;
import com.oracle.bmc.functions.requests.CreateFunctionRequest;
import com.oracle.bmc.functions.requests.InvokeFunctionRequest;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.util.StreamUtils;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests the provider-independent lifecycle features of the core against Floci OCI: {@code reset()} and the removal of
 * the sibling containers and volumes labelled with the container's resource namespace.
 */
class LifecycleServiceTest extends AbstractServiceTest {

    private static final String NAMESPACE_LABEL = "floci_namespace";

    @Test
    void shouldRemoveResourcesOnReset() {
        String bucket = "reset-" + System.nanoTime();
        try (ObjectStorageClient objectStorage = client(ObjectStorageClient.builder())) {
            objectStorage.createBucket(CreateBucketRequest.builder()
                    .namespaceName(floci.getNamespace())
                    .createBucketDetails(CreateBucketDetails.builder()
                            .name(bucket)
                            .compartmentId(tenancyId())
                            .build())
                    .build());
            GetBucketRequest getBucket = GetBucketRequest.builder()
                    .namespaceName(floci.getNamespace())
                    .bucketName(bucket)
                    .build();
            assertThat(objectStorage.getBucket(getBucket).getBucket().getName()).isEqualTo(bucket);

            floci.reset();

            assertThatThrownBy(() -> objectStorage.getBucket(getBucket))
                    .isInstanceOfSatisfying(BmcException.class, e -> assertThat(e.getStatusCode()).isEqualTo(404));
        }
    }

    @Test
    void shouldRemoveSiblingContainersAndVolumesOnStop() {
        String namespace;
        // Floci OCI reaches the Fn server sibling container by its name, which needs a user-defined network
        try (FlociOciContainer container = new FlociOciContainer(IMAGE)
                .withDedicatedNetwork()
                .disableAllServices()
                .withFunctionsConfig(c -> c.enabled(true))) {
            container.start();
            namespace = container.getResourceNamespace();
            assertThat(namespace).matches("tc-[0-9a-f]{8}");

            startFnServer(container);

            assertThat(siblingContainers(namespace)).isNotEmpty();
        }

        assertThat(siblingContainers(namespace)).isEmpty();
        assertThat(siblingVolumes(namespace)).isEmpty();
    }

    /**
     * Invokes a function, which makes Floci OCI start its Fn server sibling container. The invocation itself may
     * fail, since the image is not built with a current Fn FDK; only the Fn server matters here.
     */
    private static void startFnServer(FlociOciContainer container) {
        String functionId;
        try (FunctionsManagementClient management = client(container, FunctionsManagementClient.builder())) {
            String applicationId = management.createApplication(CreateApplicationRequest.builder()
                    .createApplicationDetails(CreateApplicationDetails.builder()
                            .compartmentId(container.getTenancyId())
                            .displayName("lifecycle")
                            .subnetIds(List.of("ocid1.subnet.oc1..testcontainers"))
                            .build())
                    .build()).getApplication().getId();
            functionId = management.createFunction(CreateFunctionRequest.builder()
                    .createFunctionDetails(CreateFunctionDetails.builder()
                            .applicationId(applicationId)
                            .displayName("hello")
                            .image("fnproject/hello:latest")
                            .memoryInMBs(128L)
                            .timeoutInSeconds(5)
                            .build())
                    .build()).getFunction().getId();
        }

        try (FunctionsInvokeClient invoke = client(container, FunctionsInvokeClient.builder())) {
            invoke.invokeFunction(InvokeFunctionRequest.builder()
                    .functionId(functionId)
                    .invokeFunctionBody(StreamUtils.createByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)))
                    .build());
        } catch (BmcException ignored) {
            // see above
        }
    }

    private static List<?> siblingContainers(String namespace) {
        return docker().listContainersCmd().withShowAll(true)
                .withLabelFilter(Map.of(NAMESPACE_LABEL, namespace)).exec();
    }

    private static List<?> siblingVolumes(String namespace) {
        return docker().listVolumesCmd().withFilter("label", List.of(NAMESPACE_LABEL + "=" + namespace))
                .exec().getVolumes();
    }

    private static DockerClient docker() {
        return DockerClientFactory.instance().client();
    }
}

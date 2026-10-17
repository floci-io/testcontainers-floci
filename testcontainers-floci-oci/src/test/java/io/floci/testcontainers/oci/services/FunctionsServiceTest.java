package io.floci.testcontainers.oci.services;

import com.oracle.bmc.functions.FunctionsInvokeClient;
import com.oracle.bmc.functions.FunctionsManagementClient;
import com.oracle.bmc.functions.model.Application;
import com.oracle.bmc.functions.model.CreateApplicationDetails;
import com.oracle.bmc.functions.model.CreateFunctionDetails;
import com.oracle.bmc.functions.model.Function;
import com.oracle.bmc.functions.requests.CreateApplicationRequest;
import com.oracle.bmc.functions.requests.CreateFunctionRequest;
import com.oracle.bmc.functions.requests.InvokeFunctionRequest;
import com.oracle.bmc.functions.requests.ListFunctionsRequest;
import com.oracle.bmc.util.StreamUtils;
import io.floci.testcontainers.oci.FlociOciContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FunctionsServiceTest extends AbstractServiceTest {

    // only stored by the management plane and the mocked invocation
    private static final String FUNCTION_IMAGE = "fnproject/hello:latest";

    @Test
    void shouldCreateAndListFunctions() {
        try (FunctionsManagementClient management = client(FunctionsManagementClient.builder())) {
            Function function = createFunction(management, tenancyId());

            var functions = management.listFunctions(ListFunctionsRequest.builder()
                    .applicationId(function.getApplicationId())
                    .build()).getItems();

            assertThat(function.getLifecycleState()).isEqualTo(Function.LifecycleState.Active);
            assertThat(functions).extracting(f -> f.getId()).containsExactly(function.getId());
        }
    }

    @Test
    void shouldInvokeMockedFunction() throws Exception {
        try (FlociOciContainer mocked = new FlociOciContainer(IMAGE)
                .disableAllServices()
                .withFunctionsConfig(c -> c.enabled(true).mock(true))) {
            mocked.start();

            String response = invoke(mocked);

            assertThat(response).contains("mock invocation of");
        }
    }

    @Test
    void shouldInvokeFunctionInFnServer() throws Exception {
        // a current FDK build (http-stream contract), answering {"message": "Hello <name>"}; older images such as
        // fnproject/hello fail to initialize in a recent Fn server
        String functionImage = buildFunctionImage();

        // Floci OCI reaches the Fn server sibling container by its name, which needs a user-defined network
        try (FlociOciContainer fn = new FlociOciContainer(IMAGE)
                .withDedicatedNetwork()
                .disableAllServices()
                .withFunctionsConfig(c -> c.enabled(true))) {
            fn.start();

            String response = invoke(fn, functionImage);

            assertThat(response).contains("Hello testcontainers");
        }
    }

    /**
     * Builds the function image from {@code src/test/resources/fn-hello}, skipping the test if that is not possible
     * (the build installs the Python FDK from PyPI), as Floci OCI's own Docker-backed Functions test does.
     */
    private static String buildFunctionImage() {
        try {
            return new ImageFromDockerfile("testcontainers-floci-oci-fn-hello")
                    .withFileFromClasspath("Dockerfile", "fn-hello/Dockerfile")
                    .withFileFromClasspath("func.py", "fn-hello/func.py")
                    .get();
        } catch (RuntimeException e) {
            assumeTrue(false, "Building the function image failed: " + e.getMessage());
            throw e;
        }
    }

    private static String invoke(FlociOciContainer container) throws Exception {
        return invoke(container, FUNCTION_IMAGE);
    }

    private static String invoke(FlociOciContainer container, String image) throws Exception {
        String functionId;
        try (FunctionsManagementClient management = client(container, FunctionsManagementClient.builder())) {
            functionId = createFunction(management, container.getTenancyId(), image).getId();
        }

        // the invoke endpoint Floci returns for a function is its own base URL
        try (FunctionsInvokeClient invoke = client(container, FunctionsInvokeClient.builder())) {
            var response = invoke.invokeFunction(InvokeFunctionRequest.builder()
                    .functionId(functionId)
                    // the SDK signs the body, so it has to be re-readable
                    .invokeFunctionBody(StreamUtils.createByteArrayInputStream(
                            "{\"name\":\"testcontainers\"}".getBytes(StandardCharsets.UTF_8)))
                    .build());
            try (InputStream body = response.getInputStream()) {
                return new String(body.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
    }

    private static Function createFunction(FunctionsManagementClient management, String compartmentId) {
        return createFunction(management, compartmentId, FUNCTION_IMAGE);
    }

    private static Function createFunction(FunctionsManagementClient management, String compartmentId, String image) {
        Application application = management.createApplication(CreateApplicationRequest.builder()
                .createApplicationDetails(CreateApplicationDetails.builder()
                        .compartmentId(compartmentId)
                        .displayName("app-" + System.nanoTime())
                        .subnetIds(List.of("ocid1.subnet.oc1..testcontainers"))
                        .build())
                .build()).getApplication();
        return management.createFunction(CreateFunctionRequest.builder()
                .createFunctionDetails(CreateFunctionDetails.builder()
                        .applicationId(application.getId())
                        .displayName("hello")
                        .image(image)
                        .memoryInMBs(128L)
                        .build())
                .build()).getFunction();
    }
}

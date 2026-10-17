package io.floci.testcontainers.services;

import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.*;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.FunctionUrlAuthType;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.model.Runtime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElbV2ServiceTest extends AbstractServiceTest {

    static ElasticLoadBalancingV2Client elbV2;
    static LambdaClient lambda;

    static String functionName;
    static URI lbFunctionUri;
    static String tgArn;
    static String lbArn;
    static String listenerArn;

    @BeforeAll
    static void setUp() {
        elbV2 = client(ElasticLoadBalancingV2Client.builder());
        lambda = client(LambdaClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateLambda() throws IOException {
        functionName = "lb-lambda-target-" + System.currentTimeMillis();
        byte[] zip = createZip("index.mjs", """
                export const handler = async (event) => {
                    return { statusCode: 200, headers: { "Content-Type": "text/plain" }, body: "Hello from Lambda!" };
                };
                """);

        lambda.createFunction(b -> b
                .functionName(functionName)
                .runtime(Runtime.NODEJS22_X)
                .role("arn:aws:iam::000000000000:role/lambda-role")
                .handler("index.handler")
                .timeout(30)
                .code(c -> c.zipFile(SdkBytes.fromByteArray(zip))));

        InvokeResponse invokeResponse = lambda.invoke(b -> b.functionName(functionName));
        assertThat(invokeResponse.statusCode()).isEqualTo(200);
        assertThat(invokeResponse.payload().asUtf8String()).contains("Hello from Lambda!");
    }

    @Test
    @Order(2)
    void shouldRegisterLambdaAsTarget() {
        String functionUrl = lambda.createFunctionUrlConfig(b -> b
                        .functionName(functionName)
                        .authType(FunctionUrlAuthType.NONE))
                .functionUrl();
        String urlId = URI.create(functionUrl).getHost().split("\\.")[0];

        // The request path /lambda-url/{urlId}/ is preserved by the ELBv2 proxy and handled
        // by Floci's LambdaUrlInvocationController, which invokes the Lambda function.
        lbFunctionUri = URI.create("http://" + floci.getHost() + ":" + floci.getMappedPort(LB_LISTENER_PORT)
                + "/lambda-url/" + urlId + "/");

        tgArn = elbV2.createTargetGroup(b -> b
                        .name("lambda-tg-" + System.currentTimeMillis())
                        .targetType(TargetTypeEnum.IP)
                        .protocol(ProtocolEnum.HTTP)
                        .port(FlociContainer.PORT))
                .targetGroups().get(0).targetGroupArn();

        elbV2.registerTargets(b -> b
                .targetGroupArn(tgArn)
                .targets(TargetDescription.builder()
                        .id("localhost")
                        .port(FlociContainer.PORT)
                        .build()));

        assertThat(functionName).isNotBlank();
        assertThat(tgArn).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldCreateLoadBalancerAndForwardToLambda() throws Exception {
        lbArn = elbV2.createLoadBalancer(b -> b
                        .name("lambda-lb-" + System.currentTimeMillis())
                        .type(LoadBalancerTypeEnum.APPLICATION)
                        .scheme(LoadBalancerSchemeEnum.INTERNET_FACING))
                .loadBalancers().get(0).loadBalancerArn();
        elbV2.waiter().waitUntilLoadBalancerAvailable(b -> b.loadBalancerArns(lbArn), c -> c.waitTimeout(Duration.ofSeconds(30)));

        listenerArn = elbV2.createListener(b -> b
                        .loadBalancerArn(lbArn)
                        .protocol(ProtocolEnum.HTTP)
                        .port(LB_LISTENER_PORT)
                        .defaultActions(Action.builder()
                                .type(ActionTypeEnum.FORWARD)
                                .targetGroupArn(tgArn)
                                .build()))
                .listeners().get(0).listenerArn();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(
                HttpRequest.newBuilder(lbFunctionUri)
                        .GET()
                        .timeout(Duration.ofSeconds(10))
                        .build(),
                BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("Hello from Lambda!");
    }

    @Test
    @Order(4)
    void shouldDeleteAllResources() {
        elbV2.deleteListener(b -> b.listenerArn(listenerArn));
        elbV2.deleteLoadBalancer(b -> b.loadBalancerArn(lbArn));
        elbV2.deleteTargetGroup(b -> b.targetGroupArn(tgArn));
        lambda.deleteFunctionUrlConfig(b -> b.functionName(functionName));
        lambda.deleteFunction(b -> b.functionName(functionName));

        assertThat(elbV2.describeListeners(b -> b.listenerArns(listenerArn)).listeners()).isEmpty();
        assertThatThrownBy(() -> elbV2.describeLoadBalancers(b -> b.loadBalancerArns(lbArn)))
                .isInstanceOf(LoadBalancerNotFoundException.class);
        assertThatThrownBy(() -> elbV2.describeTargetGroups(b -> b.targetGroupArns(tgArn)))
                .isInstanceOf(TargetGroupNotFoundException.class);
        assertThatThrownBy(() -> lambda.getFunction(b -> b.functionName(functionName)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private static byte[] createZip(String entryName, String content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry(entryName));
            zos.write(content.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}

package io.floci.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.ParameterType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlociContainerSsmServiceTest extends AbstractFlociContainerServiceTest {

    static SsmClient ssm;

    @BeforeAll
    static void setUp() {
        ssm = client(SsmClient.builder());
    }

    @Test
    void shouldPutAndGetParameter() {
        String paramName = "/test/param-" + System.currentTimeMillis();
        String paramValue = "my-parameter-value";

        ssm.putParameter(b -> b
                .name(paramName)
                .value(paramValue)
                .type(ParameterType.STRING));

        String retrieved = ssm.getParameter(b -> b.name(paramName))
                .parameter()
                .value();

        assertThat(retrieved).isEqualTo(paramValue);
    }

    @Test
    void shouldPutAndGetSecureStringParameter() {
        String paramName = "/test/secure-" + System.currentTimeMillis();
        String paramValue = "secret-value";

        ssm.putParameter(b -> b
                .name(paramName)
                .value(paramValue)
                .type(ParameterType.SECURE_STRING));

        String retrieved = ssm.getParameter(b -> b.name(paramName).withDecryption(true))
                .parameter()
                .value();

        assertThat(retrieved).isEqualTo(paramValue);
    }

    @Test
    void shouldGetParametersByPath() {
        String prefix = "/test/path-" + System.currentTimeMillis();

        ssm.putParameter(b -> b.name(prefix + "/a").value("val-a").type(ParameterType.STRING));
        ssm.putParameter(b -> b.name(prefix + "/b").value("val-b").type(ParameterType.STRING));

        List<String> names = ssm.getParametersByPath(b -> b.path(prefix).recursive(true))
                .parameters().stream()
                .map(Parameter::name)
                .toList();

        assertThat(names).containsExactlyInAnyOrder(prefix + "/a", prefix + "/b");
    }

}

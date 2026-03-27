package io.floci.testcontainers.spring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlociAwsContainerConnectionDetailsFactoryTest {

    @Test
    void shouldInstantiateFactory() {
        FlociAwsContainerConnectionDetailsFactory factory = new FlociAwsContainerConnectionDetailsFactory();
        assertThat(factory).isNotNull();
    }
}

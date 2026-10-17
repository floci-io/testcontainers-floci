package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.config.ConfigClient;
import software.amazon.awssdk.services.config.model.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class ConfigServiceTest extends AbstractServiceTest {

    private static final String RECORDER_NAME = "test-recorder-" + System.currentTimeMillis();

    static ConfigClient configService;

    @BeforeAll
    static void setUp() {
        configService = client(ConfigClient.builder());
    }

    @Test
    @Order(1)
    void shouldListConfigurationRecorders() {
        List<ConfigurationRecorder> recorders = configService.describeConfigurationRecorders().configurationRecorders();

        assertThat(recorders).isNotNull();
    }

    @Test
    @Order(2)
    void shouldPutConfigurationRecorder() {
        configService.putConfigurationRecorder(b -> b
                .configurationRecorder(ConfigurationRecorder.builder()
                        .name(RECORDER_NAME)
                        .roleARN("arn:aws:iam::123456789012:role/config-role")
                        .recordingGroup(RecordingGroup.builder()
                                .allSupported(true)
                                .includeGlobalResourceTypes(true)
                                .build())
                        .build()));

        List<String> recorderNames = configService.describeConfigurationRecorders()
                .configurationRecorders().stream()
                .map(ConfigurationRecorder::name)
                .toList();

        assertThat(recorderNames).contains(RECORDER_NAME);
    }

    @Test
    @Order(3)
    void shouldDescribeConfigurationRecorderStatus() {
        List<ConfigurationRecorderStatus> statuses = configService.describeConfigurationRecorderStatus(b -> b
                .configurationRecorderNames(RECORDER_NAME)).configurationRecordersStatus();

        assertThat(statuses).isNotNull();
        assertThat(statuses).isNotEmpty();
    }

    @Test
    @Order(4)
    void shouldDescribeConfigurationRecorder() {
        List<ConfigurationRecorder> recorders = configService.describeConfigurationRecorders(b -> b
                .configurationRecorderNames(RECORDER_NAME)).configurationRecorders();

        assertThat(recorders).hasSize(1);
        assertThat(recorders.get(0).name()).isEqualTo(RECORDER_NAME);
    }

    @Test
    @Order(5)
    void shouldStopConfigurationRecorder() {
        configService.stopConfigurationRecorder(b -> b.configurationRecorderName(RECORDER_NAME));

        List<ConfigurationRecorderStatus> statuses = configService.describeConfigurationRecorderStatus(b -> b
                .configurationRecorderNames(RECORDER_NAME)).configurationRecordersStatus();

        assertThat(statuses).isNotEmpty();
        assertThat(statuses.get(0).recording()).isFalse();
    }
}

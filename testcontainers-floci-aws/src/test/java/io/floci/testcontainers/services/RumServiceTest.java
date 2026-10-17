package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.rum.RumClient;
import software.amazon.awssdk.services.rum.model.AppMonitorSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class RumServiceTest extends AbstractServiceTest {

    static RumClient rum;

    private static final String APP_MONITOR_NAME = "test-app-monitor-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        rum = client(RumClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateAppMonitor() {
        var response = rum.createAppMonitor(b -> b
                .name(APP_MONITOR_NAME)
                .domain("example.com")
                .cwLogEnabled(true));

        assertThat(response.id()).isNotBlank();
    }

    @Test
    @Order(2)
    void shouldGetAppMonitor() {
        var response = rum.getAppMonitor(b -> b.name(APP_MONITOR_NAME));

        assertThat(response.appMonitor().name()).isEqualTo(APP_MONITOR_NAME);
        assertThat(response.appMonitor().domain()).isEqualTo("example.com");
        assertThat(response.appMonitor().dataStorage().cwLog().cwLogEnabled()).isTrue();
    }

    @Test
    @Order(3)
    void shouldListAppMonitorsContainsCreatedMonitor() {
        List<AppMonitorSummary> summaries = rum.listAppMonitors(b -> {}).appMonitorSummaries();

        assertThat(summaries).anyMatch(s -> s.name().equals(APP_MONITOR_NAME));
    }

    @Test
    @Order(4)
    void shouldUpdateAppMonitor() {
        rum.updateAppMonitor(b -> b
                .name(APP_MONITOR_NAME)
                .cwLogEnabled(false));

        var response = rum.getAppMonitor(b -> b.name(APP_MONITOR_NAME));
        assertThat(response.appMonitor().dataStorage().cwLog().cwLogEnabled()).isFalse();
    }

    @Test
    @Order(5)
    void shouldDeleteAppMonitor() {
        rum.deleteAppMonitor(b -> b.name(APP_MONITOR_NAME));

        List<AppMonitorSummary> summaries = rum.listAppMonitors(b -> {}).appMonitorSummaries();
        assertThat(summaries).noneMatch(s -> s.name().equals(APP_MONITOR_NAME));
    }
}

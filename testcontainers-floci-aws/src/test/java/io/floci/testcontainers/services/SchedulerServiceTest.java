package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.scheduler.SchedulerClient;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.ScheduleGroupSummary;
import software.amazon.awssdk.services.scheduler.model.ScheduleSummary;
import software.amazon.awssdk.services.scheduler.model.Target;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class SchedulerServiceTest extends AbstractServiceTest {

    static SchedulerClient scheduler;

    private static final String GROUP_NAME = "test-group-" + System.currentTimeMillis();
    private static final String SCHEDULE_NAME = "test-schedule-" + System.currentTimeMillis();
    private static final String TARGET_ARN = "arn:aws:sqs:us-east-1:000000000000:test-queue";

    @BeforeAll
    static void setUp() {
        scheduler = client(SchedulerClient.builder());
    }

    @Test
    @Order(1)
    void shouldListScheduleGroups() {
        List<ScheduleGroupSummary> groups = scheduler.listScheduleGroups(b -> {}).scheduleGroups();

        assertThat(groups).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateScheduleGroup() {
        scheduler.createScheduleGroup(b -> b.name(GROUP_NAME));

        List<ScheduleGroupSummary> groups = scheduler.listScheduleGroups(b -> {}).scheduleGroups();
        assertThat(groups).anyMatch(g -> g.name().equals(GROUP_NAME));
    }

    @Test
    @Order(3)
    void shouldCreateSchedule() {
        scheduler.createSchedule(b -> b
                .name(SCHEDULE_NAME)
                .groupName(GROUP_NAME)
                .scheduleExpression("rate(1 hour)")
                .flexibleTimeWindow(FlexibleTimeWindow.builder()
                        .mode(FlexibleTimeWindowMode.OFF)
                        .build())
                .target(Target.builder()
                        .arn(TARGET_ARN)
                        .roleArn("arn:aws:iam::000000000000:role/scheduler-role")
                        .build()));

        List<ScheduleSummary> schedules = scheduler.listSchedules(b -> b.groupName(GROUP_NAME)).schedules();
        assertThat(schedules).anyMatch(s -> s.name().equals(SCHEDULE_NAME));
    }

    @Test
    @Order(4)
    void shouldGetSchedule() {
        var schedule = scheduler.getSchedule(b -> b
                .name(SCHEDULE_NAME)
                .groupName(GROUP_NAME));

        assertThat(schedule.name()).isEqualTo(SCHEDULE_NAME);
        assertThat(schedule.scheduleExpression()).isEqualTo("rate(1 hour)");
    }

    @Test
    @Order(5)
    void shouldListSchedules() {
        List<ScheduleSummary> schedules = scheduler.listSchedules(b -> b.groupName(GROUP_NAME)).schedules();

        assertThat(schedules).isNotEmpty();
        assertThat(schedules).anyMatch(s -> s.name().equals(SCHEDULE_NAME));
    }

    @Test
    @Order(6)
    void shouldDeleteSchedule() {
        scheduler.deleteSchedule(b -> b.name(SCHEDULE_NAME).groupName(GROUP_NAME));

        List<ScheduleSummary> schedules = scheduler.listSchedules(b -> b.groupName(GROUP_NAME)).schedules();
        assertThat(schedules).noneMatch(s -> s.name().equals(SCHEDULE_NAME));
    }

    @Test
    @Order(7)
    void shouldDeleteScheduleGroup() {
        scheduler.deleteScheduleGroup(b -> b.name(GROUP_NAME));

        List<ScheduleGroupSummary> groups = scheduler.listScheduleGroups(b -> {}).scheduleGroups();
        assertThat(groups).noneMatch(g -> g.name().equals(GROUP_NAME));
    }
}

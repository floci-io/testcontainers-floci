package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.swf.SwfClient;
import software.amazon.awssdk.services.swf.model.ChildPolicy;
import software.amazon.awssdk.services.swf.model.CloseStatus;
import software.amazon.awssdk.services.swf.model.Decision;
import software.amazon.awssdk.services.swf.model.DecisionType;
import software.amazon.awssdk.services.swf.model.ExecutionStatus;
import software.amazon.awssdk.services.swf.model.PollForActivityTaskResponse;
import software.amazon.awssdk.services.swf.model.PollForDecisionTaskResponse;
import software.amazon.awssdk.services.swf.model.WorkflowExecutionInfo;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(OrderAnnotation.class)
class SwfServiceTest extends AbstractServiceTest {

    static SwfClient swf;

    private static final String DOMAIN = "test-domain-" + System.currentTimeMillis();
    private static final String WORKFLOW_TYPE_NAME = "TestWorkflow";
    private static final String WORKFLOW_TYPE_VERSION = "1.0";
    private static final String ACTIVITY_TYPE_NAME = "TestActivity";
    private static final String ACTIVITY_TYPE_VERSION = "1.0";
    private static final String TASK_LIST = "test-task-list";
    private static final String WORKFLOW_ID = "test-workflow-" + System.currentTimeMillis();

    static String runId;

    @BeforeAll
    static void setUp() {
        swf = client(SwfClient.builder());
    }

    @Test
    @Order(1)
    void shouldRegisterDomain() {
        swf.registerDomain(b -> b
                .name(DOMAIN)
                .workflowExecutionRetentionPeriodInDays("1"));

        var response = swf.describeDomain(b -> b.name(DOMAIN));
        assertThat(response.domainInfo().name()).isEqualTo(DOMAIN);
    }

    @Test
    @Order(2)
    void shouldRegisterWorkflowAndActivityType() {
        swf.registerWorkflowType(b -> b
                .domain(DOMAIN)
                .name(WORKFLOW_TYPE_NAME)
                .version(WORKFLOW_TYPE_VERSION)
                .defaultTaskList(tl -> tl.name(TASK_LIST))
                .defaultTaskStartToCloseTimeout("30")
                .defaultExecutionStartToCloseTimeout("60")
                .defaultChildPolicy(ChildPolicy.TERMINATE));

        swf.registerActivityType(b -> b
                .domain(DOMAIN)
                .name(ACTIVITY_TYPE_NAME)
                .version(ACTIVITY_TYPE_VERSION)
                .defaultTaskList(tl -> tl.name(TASK_LIST))
                .defaultTaskStartToCloseTimeout("30")
                .defaultTaskHeartbeatTimeout("30")
                .defaultTaskScheduleToStartTimeout("30")
                .defaultTaskScheduleToCloseTimeout("60"));

        var workflowType = swf.describeWorkflowType(b -> b
                .domain(DOMAIN)
                .workflowType(t -> t.name(WORKFLOW_TYPE_NAME).version(WORKFLOW_TYPE_VERSION)));
        assertThat(workflowType.typeInfo().workflowType().name()).isEqualTo(WORKFLOW_TYPE_NAME);

        var activityType = swf.describeActivityType(b -> b
                .domain(DOMAIN)
                .activityType(t -> t.name(ACTIVITY_TYPE_NAME).version(ACTIVITY_TYPE_VERSION)));
        assertThat(activityType.typeInfo().activityType().name()).isEqualTo(ACTIVITY_TYPE_NAME);
    }

    @Test
    @Order(3)
    void shouldStartWorkflowExecution() {
        var response = swf.startWorkflowExecution(b -> b
                .domain(DOMAIN)
                .workflowId(WORKFLOW_ID)
                .workflowType(t -> t.name(WORKFLOW_TYPE_NAME).version(WORKFLOW_TYPE_VERSION))
                .taskList(tl -> tl.name(TASK_LIST))
                .input("start-input"));

        runId = response.runId();
        assertThat(runId).isNotBlank();

        var execution = swf.describeWorkflowExecution(b -> b
                .domain(DOMAIN)
                .execution(e -> e.workflowId(WORKFLOW_ID).runId(runId)));
        assertThat(execution.executionInfo().executionStatus()).isEqualTo(ExecutionStatus.OPEN);
    }

    @Test
    @Order(4)
    void shouldPollForDecisionTaskAndScheduleActivity() {
        PollForDecisionTaskResponse task = swf.pollForDecisionTask(b -> b
                .domain(DOMAIN)
                .taskList(tl -> tl.name(TASK_LIST)));

        assertThat(task.taskToken()).isNotBlank();

        Decision scheduleActivity = Decision.builder()
                .decisionType(DecisionType.SCHEDULE_ACTIVITY_TASK)
                .scheduleActivityTaskDecisionAttributes(a -> a
                        .activityType(t -> t.name(ACTIVITY_TYPE_NAME).version(ACTIVITY_TYPE_VERSION))
                        .activityId("test-activity-1")
                        .taskList(tl -> tl.name(TASK_LIST))
                        .input("activity-input"))
                .build();

        swf.respondDecisionTaskCompleted(b -> b
                .taskToken(task.taskToken())
                .decisions(scheduleActivity));
    }

    @Test
    @Order(5)
    void shouldPollForActivityTaskAndComplete() {
        PollForActivityTaskResponse task = swf.pollForActivityTask(b -> b
                .domain(DOMAIN)
                .taskList(tl -> tl.name(TASK_LIST)));

        assertThat(task.taskToken()).isNotBlank();
        assertThat(task.activityId()).isEqualTo("test-activity-1");
        assertThat(task.input()).isEqualTo("activity-input");

        swf.respondActivityTaskCompleted(b -> b
                .taskToken(task.taskToken())
                .result("activity-result"));
    }

    @Test
    @Order(6)
    void shouldPollForDecisionTaskAndCompleteWorkflow() {
        PollForDecisionTaskResponse task = swf.pollForDecisionTask(b -> b
                .domain(DOMAIN)
                .taskList(tl -> tl.name(TASK_LIST)));

        assertThat(task.taskToken()).isNotBlank();

        Decision completeWorkflow = Decision.builder()
                .decisionType(DecisionType.COMPLETE_WORKFLOW_EXECUTION)
                .completeWorkflowExecutionDecisionAttributes(a -> a.result("workflow-result"))
                .build();

        swf.respondDecisionTaskCompleted(b -> b
                .taskToken(task.taskToken())
                .decisions(completeWorkflow));

        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    var execution = swf.describeWorkflowExecution(b -> b
                            .domain(DOMAIN)
                            .execution(e -> e.workflowId(WORKFLOW_ID).runId(runId)));
                    assertThat(execution.executionInfo().executionStatus()).isEqualTo(ExecutionStatus.CLOSED);
                    assertThat(execution.executionInfo().closeStatus()).isEqualTo(CloseStatus.COMPLETED);
                });
    }

    @Test
    @Order(7)
    void shouldGetWorkflowExecutionHistory() {
        var history = swf.getWorkflowExecutionHistory(b -> b
                .domain(DOMAIN)
                .execution(e -> e.workflowId(WORKFLOW_ID).runId(runId)));

        assertThat(history.events()).isNotEmpty();
    }

    @Test
    @Order(8)
    void shouldListClosedWorkflowExecutionsContainsCompletedExecution() {
        List<WorkflowExecutionInfo> executions = swf.listClosedWorkflowExecutions(b -> b
                        .domain(DOMAIN)
                        .startTimeFilter(f -> f.oldestDate(Instant.now().minusSeconds(600))))
                .executionInfos();

        assertThat(executions).anyMatch(e -> e.execution().workflowId().equals(WORKFLOW_ID));
    }

    @Test
    @Order(9)
    void shouldTagAndListTagsForDomain() {
        String domainArn = "arn:aws:swf:" + floci.getRegion() + ":" + floci.getDefaultAccountId() + ":/domain/" + DOMAIN;

        swf.tagResource(b -> b.resourceArn(domainArn).tags(t -> t.key("env").value("test")));

        var tags = swf.listTagsForResource(b -> b.resourceArn(domainArn));
        assertThat(tags.tags()).anyMatch(t -> t.key().equals("env") && t.value().equals("test"));

        swf.untagResource(b -> b.resourceArn(domainArn).tagKeys("env"));
        tags = swf.listTagsForResource(b -> b.resourceArn(domainArn));
        assertThat(tags.tags()).noneMatch(t -> t.key().equals("env"));
    }

    @Test
    @Order(10)
    void shouldDeprecateWorkflowTypeAndActivityTypeAndDomain() {
        swf.deprecateWorkflowType(b -> b
                .domain(DOMAIN)
                .workflowType(t -> t.name(WORKFLOW_TYPE_NAME).version(WORKFLOW_TYPE_VERSION)));
        swf.deprecateActivityType(b -> b
                .domain(DOMAIN)
                .activityType(t -> t.name(ACTIVITY_TYPE_NAME).version(ACTIVITY_TYPE_VERSION)));
        swf.deprecateDomain(b -> b.name(DOMAIN));

        var domain = swf.describeDomain(b -> b.name(DOMAIN));
        assertThat(domain.domainInfo().status().toString()).isEqualTo("DEPRECATED");
    }
}

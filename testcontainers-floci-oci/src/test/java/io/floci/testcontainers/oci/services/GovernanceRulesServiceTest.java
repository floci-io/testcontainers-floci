package io.floci.testcontainers.oci.services;

import com.oracle.bmc.governancerulescontrolplane.GovernanceRuleClient;
import com.oracle.bmc.governancerulescontrolplane.WorkRequestClient;
import com.oracle.bmc.governancerulescontrolplane.model.AllowedRegionsTemplate;
import com.oracle.bmc.governancerulescontrolplane.model.CreateGovernanceRuleDetails;
import com.oracle.bmc.governancerulescontrolplane.model.CreateInclusionCriterionDetails;
import com.oracle.bmc.governancerulescontrolplane.model.CreationOption;
import com.oracle.bmc.governancerulescontrolplane.model.GovernanceRuleSummary;
import com.oracle.bmc.governancerulescontrolplane.model.GovernanceRuleType;
import com.oracle.bmc.governancerulescontrolplane.model.InclusionCriterion;
import com.oracle.bmc.governancerulescontrolplane.model.TenancyAssociation;
import com.oracle.bmc.governancerulescontrolplane.requests.CreateGovernanceRuleRequest;
import com.oracle.bmc.governancerulescontrolplane.requests.CreateInclusionCriterionRequest;
import com.oracle.bmc.governancerulescontrolplane.requests.GetWorkRequestRequest;
import com.oracle.bmc.governancerulescontrolplane.requests.ListGovernanceRulesRequest;
import com.oracle.bmc.governancerulescontrolplane.requests.ListInclusionCriteriaRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GovernanceRulesServiceTest extends AbstractServiceTest {

    private static GovernanceRuleClient rules;
    private static WorkRequestClient workRequests;

    @BeforeAll
    static void createClients() {
        rules = client(GovernanceRuleClient.builder());
        workRequests = client(WorkRequestClient.builder());
    }

    @AfterAll
    static void closeClients() {
        rules.close();
        workRequests.close();
    }

    @Test
    void shouldCreateAllowedRegionsRule() {
        // governance rules live in the root compartment of a tenancy that is not a child tenancy
        var created = rules.createGovernanceRule(CreateGovernanceRuleRequest.builder()
                .createGovernanceRuleDetails(CreateGovernanceRuleDetails.builder()
                        .compartmentId(tenancyId())
                        .displayName("allowed-regions-" + System.nanoTime())
                        .type(GovernanceRuleType.AllowedRegions)
                        .creationOption(CreationOption.Template)
                        .template(AllowedRegionsTemplate.builder()
                                .displayName("only-default-region")
                                .regions(List.of(floci.getRegion()))
                                .build())
                        .build())
                .build());
        String ruleId = created.getGovernanceRule().getId();
        assertThat(created.getGovernanceRule().getTemplate()).isInstanceOf(AllowedRegionsTemplate.class);

        var workRequest = workRequests.getWorkRequest(GetWorkRequestRequest.builder()
                .workRequestId(created.getOpcWorkRequestId())
                .build()).getWorkRequest();
        assertThat(workRequest.getStatus().getValue()).isEqualTo("SUCCEEDED");
        assertThat(workRequest.getOperationType().getValue()).isEqualTo("CREATE_GOVERNANCE_RULE");

        rules.createInclusionCriterion(CreateInclusionCriterionRequest.builder()
                .createInclusionCriterionDetails(CreateInclusionCriterionDetails.builder()
                        .governanceRuleId(ruleId)
                        .type(InclusionCriterion.Type.Tenancy)
                        .association(TenancyAssociation.builder()
                                .tenancyId("ocid1.tenancy.oc1..testcontainerschild")
                                .build())
                        .build())
                .build());

        assertThat(rules.listInclusionCriteria(ListInclusionCriteriaRequest.builder()
                .governanceRuleId(ruleId)
                .build()).getInclusionCriterionCollection().getItems()).hasSize(1);
        assertThat(rules.listGovernanceRules(ListGovernanceRulesRequest.builder()
                .compartmentId(tenancyId())
                .build()).getGovernanceRuleCollection().getItems())
                .extracting(GovernanceRuleSummary::getId).contains(ruleId);
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.networkfirewall.NetworkFirewallClient;
import software.amazon.awssdk.services.networkfirewall.model.RuleGroupType;
import software.amazon.awssdk.services.networkfirewall.model.RuleGroupMetadata;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class NetworkFirewallServiceTest extends AbstractServiceTest {

    private static final String RULE_GROUP_NAME = "floci-tc-allow-list";

    static NetworkFirewallClient networkFirewall;

    static String ruleGroupArn;

    @BeforeAll
    static void setUp() {
        networkFirewall = client(NetworkFirewallClient.builder());
    }

    @Test
    @Order(1)
    void shouldCreateRuleGroup() {
        var response = networkFirewall.createRuleGroup(b -> b
                .ruleGroupName(RULE_GROUP_NAME)
                .type(RuleGroupType.STATEFUL)
                .capacity(100)
                .ruleGroup(rg -> rg.rulesSource(rs -> rs.rulesString("pass ip any any"))));

        assertThat(response.ruleGroupResponse().ruleGroupName()).isEqualTo(RULE_GROUP_NAME);
        assertThat(response.ruleGroupResponse().ruleGroupArn()).contains(":stateful-rulegroup/");
        ruleGroupArn = response.ruleGroupResponse().ruleGroupArn();
    }

    @Test
    @Order(2)
    void shouldDescribeRuleGroup() {
        var response = networkFirewall.describeRuleGroup(b -> b.ruleGroupArn(ruleGroupArn));

        assertThat(response.ruleGroupResponse().ruleGroupName()).isEqualTo(RULE_GROUP_NAME);
    }

    @Test
    @Order(3)
    void shouldListRuleGroupsContainsCreatedRuleGroup() {
        var response = networkFirewall.listRuleGroups(b -> b.type(RuleGroupType.STATEFUL));

        assertThat(response.ruleGroups())
                .extracting(RuleGroupMetadata::name)
                .contains(RULE_GROUP_NAME);
    }

    @Test
    @Order(4)
    void shouldDeleteRuleGroup() {
        networkFirewall.deleteRuleGroup(b -> b.ruleGroupArn(ruleGroupArn));

        var response = networkFirewall.listRuleGroups(b -> b.type(RuleGroupType.STATEFUL));
        assertThat(response.ruleGroups())
                .extracting(RuleGroupMetadata::name)
                .doesNotContain(RULE_GROUP_NAME);
    }
}

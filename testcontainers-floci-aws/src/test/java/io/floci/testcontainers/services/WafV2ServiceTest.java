package io.floci.testcontainers.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import software.amazon.awssdk.services.wafv2.Wafv2Client;
import software.amazon.awssdk.services.wafv2.model.AllowAction;
import software.amazon.awssdk.services.wafv2.model.CreateWebAclResponse;
import software.amazon.awssdk.services.wafv2.model.DefaultAction;
import software.amazon.awssdk.services.wafv2.model.GetWebAclResponse;
import software.amazon.awssdk.services.wafv2.model.Scope;
import software.amazon.awssdk.services.wafv2.model.VisibilityConfig;
import software.amazon.awssdk.services.wafv2.model.WebACLSummary;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class WafV2ServiceTest extends AbstractServiceTest {

    static Wafv2Client wafv2;
    static String webAclId;
    static String webAclLockToken;

    private static final String WEB_ACL_NAME = "test-web-acl-" + System.currentTimeMillis();

    @BeforeAll
    static void setUp() {
        wafv2 = client(Wafv2Client.builder());
    }

    @Test
    @Order(1)
    void shouldListWebAcls() {
        List<WebACLSummary> acls = wafv2.listWebACLs(b -> b.scope(Scope.REGIONAL)).webACLs();

        assertThat(acls).isNotNull();
    }

    @Test
    @Order(2)
    void shouldCreateWebAcl() {
        CreateWebAclResponse response = wafv2.createWebACL(b -> b
                .name(WEB_ACL_NAME)
                .scope(Scope.REGIONAL)
                .defaultAction(DefaultAction.builder()
                        .allow(AllowAction.builder().build())
                        .build())
                .visibilityConfig(VisibilityConfig.builder()
                        .cloudWatchMetricsEnabled(false)
                        .metricName(WEB_ACL_NAME)
                        .sampledRequestsEnabled(false)
                        .build()));

        webAclId = response.summary().id();
        webAclLockToken = response.summary().lockToken();
        assertThat(webAclId).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldListWebAclsContainsCreatedAcl() {
        List<WebACLSummary> acls = wafv2.listWebACLs(b -> b.scope(Scope.REGIONAL)).webACLs();

        assertThat(acls).anyMatch(a -> a.name().equals(WEB_ACL_NAME));
    }

    @Test
    @Order(4)
    void shouldGetWebAcl() {
        GetWebAclResponse response = wafv2.getWebACL(b -> b
                .name(WEB_ACL_NAME)
                .scope(Scope.REGIONAL)
                .id(webAclId));

        assertThat(response.webACL().name()).isEqualTo(WEB_ACL_NAME);
        webAclLockToken = response.lockToken();
    }

    @Test
    @Order(5)
    void shouldDeleteWebAcl() {
        wafv2.deleteWebACL(b -> b
                .name(WEB_ACL_NAME)
                .scope(Scope.REGIONAL)
                .id(webAclId)
                .lockToken(webAclLockToken));

        List<WebACLSummary> acls = wafv2.listWebACLs(b -> b.scope(Scope.REGIONAL)).webACLs();
        assertThat(acls).noneMatch(a -> a.name().equals(WEB_ACL_NAME));
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.lakeformation.LakeFormationClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class LakeFormationServiceTest extends AbstractServiceTest {

    private static final String ADMIN_ARN = "arn:aws:iam::000000000000:user/lake-admin";
    private static final String TAG_KEY = "floci-tc-department";

    static LakeFormationClient lakeFormation;

    @BeforeAll
    static void setUp() {
        lakeFormation = client(LakeFormationClient.builder());
    }

    @Test
    @Order(1)
    void shouldPutAndGetDataLakeSettings() {
        lakeFormation.putDataLakeSettings(b -> b.dataLakeSettings(s -> s
                .dataLakeAdmins(a -> a.dataLakePrincipalIdentifier(ADMIN_ARN))));

        var response = lakeFormation.getDataLakeSettings(b -> {});

        assertThat(response.dataLakeSettings().dataLakeAdmins())
                .anyMatch(a -> ADMIN_ARN.equals(a.dataLakePrincipalIdentifier()));
    }

    @Test
    @Order(2)
    void shouldCreateAndGetLfTag() {
        lakeFormation.createLFTag(b -> b.tagKey(TAG_KEY).tagValues("sales", "engineering"));

        var response = lakeFormation.getLFTag(b -> b.tagKey(TAG_KEY));

        assertThat(response.tagKey()).isEqualTo(TAG_KEY);
        assertThat(response.tagValues()).containsExactlyInAnyOrder("sales", "engineering");
    }

    @Test
    @Order(3)
    void shouldDeleteLfTag() {
        lakeFormation.deleteLFTag(b -> b.tagKey(TAG_KEY));

        var response = lakeFormation.listLFTags(b -> {});
        assertThat(response.lfTags()).noneMatch(t -> TAG_KEY.equals(t.tagKey()));
    }
}

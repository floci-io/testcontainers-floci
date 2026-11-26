package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.signer.SignerClient;
import software.amazon.awssdk.services.signer.model.DescribeSigningJobResponse;
import software.amazon.awssdk.services.signer.model.GetSigningProfileResponse;
import software.amazon.awssdk.services.signer.model.SigningJob;
import software.amazon.awssdk.services.signer.model.SigningPlatform;
import software.amazon.awssdk.services.signer.model.SigningProfileStatus;
import software.amazon.awssdk.services.signer.model.SigningStatus;
import software.amazon.awssdk.services.signer.model.ValidityType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class SignerServiceTest extends AbstractServiceTest {

    private static final String PLATFORM_LAMBDA = "AWSLambda-SHA384-ECDSA";

    static SignerClient signer;

    static String profileName;

    @BeforeAll
    static void setUp() {
        signer = client(SignerClient.builder());
    }

    @Test
    @Order(1)
    void shouldListSigningPlatforms() {
        assertThat(signer.listSigningPlatforms(b -> { }).platforms())
                .extracting(SigningPlatform::platformId)
                .contains(PLATFORM_LAMBDA);
    }

    @Test
    @Order(2)
    void shouldPutAndGetSigningProfile() {
        profileName = "testcontainers_" + System.currentTimeMillis();
        String arn = signer.putSigningProfile(b -> b
                        .profileName(profileName)
                        .platformId(PLATFORM_LAMBDA)
                        .signatureValidityPeriod(v -> v.type(ValidityType.DAYS).value(135))
                        .tags(Map.of("env", "test")))
                .arn();

        GetSigningProfileResponse profile = signer.getSigningProfile(b -> b.profileName(profileName));

        assertThat(arn).endsWith(":/signing-profiles/" + profileName);
        assertThat(profile.platformId()).isEqualTo(PLATFORM_LAMBDA);
        assertThat(profile.status()).isEqualTo(SigningProfileStatus.ACTIVE);
        assertThat(profile.signatureValidityPeriod().value()).isEqualTo(135);
        assertThat(profile.tags()).containsEntry("env", "test");
    }

    @Test
    @Order(3)
    void shouldRecordSigningJobAsFailedWithoutMockedSigning() {
        String jobId = signer.startSigningJob(b -> b
                        .profileName(profileName)
                        .source(s -> s.s3(s3 -> s3.bucketName("artifacts").key("app.zip").version("1")))
                        .destination(d -> d.s3(s3 -> s3.bucketName("signed").prefix("out/")))
                        .clientRequestToken("testcontainers-token"))
                .jobId();

        DescribeSigningJobResponse job = signer.describeSigningJob(b -> b.jobId(jobId));

        // Floci does not sign code: unless mockSigningJobs is enabled, a job fails without an artifact
        assertThat(job.status()).isEqualTo(SigningStatus.FAILED);
        assertThat(job.profileName()).isEqualTo(profileName);
        assertThat(job.signedObject()).isNull();
        assertThat(signer.listSigningJobs(b -> b.status(SigningStatus.FAILED)).jobs())
                .extracting(SigningJob::jobId)
                .contains(jobId);
    }

    @Test
    @Order(4)
    void shouldCancelSigningProfile() {
        signer.cancelSigningProfile(b -> b.profileName(profileName));

        assertThat(signer.getSigningProfile(b -> b.profileName(profileName)).status())
                .isEqualTo(SigningProfileStatus.CANCELED);
    }
}

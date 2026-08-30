package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.LanguageCode;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobSummary;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class TranscribeServiceTest extends AbstractServiceTest {

    private static final String JOB_NAME = "floci-tc-transcription-job";
    private static final String MEDIA_URI = "s3://my-bucket/audio.mp3";

    static TranscribeClient transcribe;

    @BeforeAll
    static void setUp() {
        transcribe = client(TranscribeClient.builder());
    }

    @Test
    @Order(1)
    void shouldStartTranscriptionJob() {
        var response = transcribe.startTranscriptionJob(b -> b
                .transcriptionJobName(JOB_NAME)
                .languageCode(LanguageCode.EN_US)
                .media(m -> m.mediaFileUri(MEDIA_URI)));

        assertThat(response.transcriptionJob().transcriptionJobName()).isEqualTo(JOB_NAME);
        assertThat(response.transcriptionJob().transcriptionJobStatus()).isNotNull();
    }

    @Test
    @Order(2)
    void shouldGetTranscriptionJob() {
        var response = transcribe.getTranscriptionJob(b -> b.transcriptionJobName(JOB_NAME));

        assertThat(response.transcriptionJob().transcriptionJobName()).isEqualTo(JOB_NAME);
        assertThat(response.transcriptionJob().languageCode()).isEqualTo(LanguageCode.EN_US);
        assertThat(response.transcriptionJob().transcriptionJobStatus()).isEqualTo(TranscriptionJobStatus.COMPLETED);
        assertThat(response.transcriptionJob().transcript().transcriptFileUri()).isNotBlank();
    }

    @Test
    @Order(3)
    void shouldListTranscriptionJobsContainingCreatedJob() {
        var response = transcribe.listTranscriptionJobs(b -> b.jobNameContains(JOB_NAME));

        assertThat(response.transcriptionJobSummaries())
                .extracting(TranscriptionJobSummary::transcriptionJobName)
                .contains(JOB_NAME);
    }

    @Test
    @Order(4)
    void shouldDeleteTranscriptionJob() {
        transcribe.deleteTranscriptionJob(b -> b.transcriptionJobName(JOB_NAME));

        var response = transcribe.listTranscriptionJobs(b -> b.jobNameContains(JOB_NAME));
        assertThat(response.transcriptionJobSummaries())
                .extracting(TranscriptionJobSummary::transcriptionJobName)
                .doesNotContain(JOB_NAME);
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.comprehend.ComprehendClient;

import static org.assertj.core.api.Assertions.assertThat;

class ComprehendServiceTest extends AbstractServiceTest {

    private static final String TEXT = "Floci makes local AWS testing painless";

    static ComprehendClient comprehend;

    @BeforeAll
    static void setUp() {
        comprehend = client(ComprehendClient.builder());
    }

    @Test
    void shouldDetectSentiment() {
        var response = comprehend.detectSentiment(b -> b.text(TEXT).languageCode("en"));

        assertThat(response.sentimentAsString()).isNotBlank();
        assertThat(response.sentimentScore()).isNotNull();
    }

    @Test
    void shouldDetectDominantLanguage() {
        var response = comprehend.detectDominantLanguage(b -> b.text(TEXT));

        assertThat(response.languages()).isNotEmpty();
        assertThat(response.languages().get(0).languageCode()).isEqualTo("en");
    }

    @Test
    void shouldDetectKeyPhrases() {
        var response = comprehend.detectKeyPhrases(b -> b.text(TEXT).languageCode("en"));

        assertThat(response.keyPhrases()).isNotEmpty();
    }
}

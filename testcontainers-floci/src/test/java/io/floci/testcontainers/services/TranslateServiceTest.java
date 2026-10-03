package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.Language;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import static org.assertj.core.api.Assertions.assertThat;

class TranslateServiceTest extends AbstractServiceTest {

    static TranslateClient translate;

    @BeforeAll
    static void setUp() {
        translate = client(TranslateClient.builder());
    }

    @Test
    void shouldTranslateText() {
        TranslateTextResponse response = translate.translateText(b -> b
                .text("Hello world")
                .sourceLanguageCode("en")
                .targetLanguageCode("de"));

        // Floci's Translate is a mock that echoes the input text
        assertThat(response.translatedText()).isEqualTo("Hello world");
        assertThat(response.sourceLanguageCode()).isEqualTo("en");
        assertThat(response.targetLanguageCode()).isEqualTo("de");
    }

    @Test
    void shouldListLanguages() {
        assertThat(translate.listLanguages(b -> {}).languages())
                .extracting(Language::languageCode)
                .contains("en", "de");
    }
}

package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;

import static org.assertj.core.api.Assertions.assertThat;

class RekognitionServiceTest extends AbstractServiceTest {

    private static final Image IMAGE = Image.builder()
            .s3Object(S3Object.builder().bucket("my-bucket").name("photo.jpg").build())
            .build();

    static RekognitionClient rekognition;

    @BeforeAll
    static void setUp() {
        rekognition = client(RekognitionClient.builder());
    }

    @Test
    void shouldDetectLabels() {
        var response = rekognition.detectLabels(b -> b.image(IMAGE));

        assertThat(response.labels()).isNotEmpty();
        assertThat(response.labelModelVersion()).isNotBlank();
    }

    @Test
    void shouldDetectText() {
        var response = rekognition.detectText(b -> b.image(IMAGE));

        assertThat(response.textDetections()).isNotEmpty();
    }

    @Test
    void shouldDetectFaces() {
        var response = rekognition.detectFaces(b -> b.image(IMAGE));

        assertThat(response.faceDetails()).isNotNull();
    }
}

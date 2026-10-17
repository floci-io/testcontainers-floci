package io.floci.testcontainers.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.s3.S3Client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RekognitionServiceTest extends AbstractServiceTest {

    private static final String BUCKET = "floci-tc-rekognition";
    private static final String KEY = "photo.png";

    private static final Image IMAGE = Image.builder()
            .s3Object(S3Object.builder().bucket(BUCKET).name(KEY).build())
            .build();

    static RekognitionClient rekognition;

    @BeforeAll
    static void setUp() throws IOException {
        rekognition = client(RekognitionClient.builder());

        // Floci reads the S3 object and rejects anything that is not a complete JPEG or PNG
        S3Client s3 = client(S3Client.builder().forcePathStyle(true));
        s3.createBucket(b -> b.bucket(BUCKET));
        s3.putObject(b -> b.bucket(BUCKET).key(KEY).contentType("image/png"), RequestBody.fromBytes(png()));
    }

    private static byte[] png() throws IOException {
        var out = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB), "png", out);
        return out.toByteArray();
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

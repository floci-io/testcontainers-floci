package io.floci.testcontainers.core.testing;

import io.floci.testcontainers.core.CloudDescriptor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Returns the pinned emulator images the integration tests run against, as listed in {@code .github/docker-images.txt}
 * at the root of the repository (one {@code image:tag} per line, {@code #} comments and blank lines ignored). CI
 * pre-pulls the same file, so each tag is declared once.
 *
 * <p>The system property {@value #TAG_OVERRIDE_PROPERTY} replaces the tag of every image for a local run, e.g.
 * {@code mvn verify -Dfloci.test-image.tag=nightly} to check a Floci feature that is not released yet.
 */
public final class TestImages {

    /**
     * System property that replaces the pinned tag of every image (e.g. {@code nightly}).
     */
    public static final String TAG_OVERRIDE_PROPERTY = "floci.test-image.tag";

    private static final Path IMAGES_FILE = Path.of(".github", "docker-images.txt");

    private static Map<String, String> pinnedImages;

    private TestImages() {
    }

    /**
     * Returns the pinned image of a cloud, e.g. {@code floci/floci-az:x.y.z} for the Azure descriptor.
     *
     * @param descriptor the descriptor of the cloud, whose {@link CloudDescriptor#getImage()} is looked up
     * @return the pinned image including its tag
     * @throws IllegalStateException if the images file cannot be found or does not list the image
     */
    public static String pinnedImage(CloudDescriptor descriptor) {
        String tagOverride = System.getProperty(TAG_OVERRIDE_PROPERTY);
        if (tagOverride != null && !tagOverride.isBlank()) {
            return descriptor.getImage() + ":" + tagOverride.strip();
        }
        String image = pinnedImages().get(descriptor.getImage());
        if (image == null) {
            throw new IllegalStateException(IMAGES_FILE + " lists no tag for " + descriptor.getImage());
        }
        return image;
    }

    private static synchronized Map<String, String> pinnedImages() {
        if (pinnedImages == null) {
            pinnedImages = parse(findImagesFile());
        }
        return pinnedImages;
    }

    /**
     * Looks for the images file in the working directory (the module directory under Maven) and its parents.
     */
    private static Path findImagesFile() {
        for (Path directory = Path.of("").toAbsolutePath(); directory != null; directory = directory.getParent()) {
            Path candidate = directory.resolve(IMAGES_FILE);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Cannot find " + IMAGES_FILE + " in " + Path.of("").toAbsolutePath()
                + " or any parent directory");
    }

    private static Map<String, String> parse(Path file) {
        Map<String, String> images = new LinkedHashMap<>();
        try {
            for (String line : Files.readAllLines(file)) {
                String entry = line.strip();
                if (entry.isEmpty() || entry.startsWith("#")) {
                    continue;
                }
                int tagSeparator = entry.lastIndexOf(':');
                if (tagSeparator <= 0 || entry.indexOf('/', tagSeparator) >= 0) {
                    throw new IllegalStateException(file + ": '" + entry + "' is not an image:tag");
                }
                images.put(entry.substring(0, tagSeparator), entry);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + file, e);
        }
        return images;
    }
}

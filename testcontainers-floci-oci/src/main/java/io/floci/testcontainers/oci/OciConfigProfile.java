package io.floci.testcontainers.oci;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A profile of an OCI SDK/CLI configuration file ({@code ~/.oci/config} format) for a Floci OCI container, as returned
 * by {@link FlociOciContainer#getConfigProfile()}.
 *
 * <p>The OCI SDKs' file-based authentication can load it directly:
 * <pre>{@code
 * OciConfigProfile profile = floci.getConfigProfile();
 * AuthenticationDetailsProvider auth = new ConfigFileAuthenticationDetailsProvider(
 *     profile.writeConfigFile().toString(), profile.getName());
 * ObjectStorageClient client = ObjectStorageClient.builder()
 *     .endpoint(floci.getEndpoint())
 *     .build(auth);
 * }</pre>
 */
public final class OciConfigProfile {

    /**
     * Name of the profile, the default profile of OCI configuration files.
     */
    public static final String DEFAULT_PROFILE_NAME = "DEFAULT";

    private final String name;
    private final String user;
    private final String fingerprint;
    private final String tenancy;
    private final String region;
    private final Path keyFile;

    OciConfigProfile(String name, String user, String fingerprint, String tenancy, String region, Path keyFile) {
        this.name = Objects.requireNonNull(name, "name");
        this.user = Objects.requireNonNull(user, "user");
        this.fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        this.tenancy = Objects.requireNonNull(tenancy, "tenancy");
        this.region = Objects.requireNonNull(region, "region");
        this.keyFile = Objects.requireNonNull(keyFile, "keyFile");
    }

    /**
     * Returns the name of the profile ({@value DEFAULT_PROFILE_NAME}).
     *
     * @return the profile name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the OCID of the user the requests are signed for.
     *
     * @return the user OCID
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the fingerprint of the public key (MD5 of its DER encoding, colon-separated hex), as the OCI console
     * shows it for an API key.
     *
     * @return the key fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }

    /**
     * Returns the OCID of the tenancy, the one of the container ({@link FlociOciContainer#getTenancyId()}).
     *
     * @return the tenancy OCID
     */
    public String getTenancy() {
        return tenancy;
    }

    /**
     * Returns the region identifier, the one of the container ({@link FlociOciContainer#getRegion()}).
     *
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Returns the PEM file (PKCS#8) holding the throwaway RSA private key the requests are signed with.
     *
     * @return the private key file
     */
    public Path getKeyFile() {
        return keyFile;
    }

    /**
     * Renders this profile as a section of an OCI configuration file.
     *
     * @return the configuration file content
     */
    public String toConfigFileContent() {
        return "[" + name + "]\n"
                + "user=" + user + "\n"
                + "fingerprint=" + fingerprint + "\n"
                + "tenancy=" + tenancy + "\n"
                + "region=" + region + "\n"
                + "key_file=" + keyFile.toAbsolutePath() + "\n";
    }

    /**
     * Writes this profile to a new temporary OCI configuration file, which is deleted when the JVM exits.
     *
     * @return the configuration file
     * @throws UncheckedIOException if the file cannot be written
     */
    public Path writeConfigFile() {
        try {
            Path file = Files.createTempFile("floci-oci-config-", "");
            file.toFile().deleteOnExit();
            return writeConfigFile(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to create an OCI configuration file", e);
        }
    }

    /**
     * Writes this profile to the given OCI configuration file, replacing its content.
     *
     * @param configFile the file to write
     * @return {@code configFile}
     * @throws UncheckedIOException if the file cannot be written
     */
    public Path writeConfigFile(Path configFile) {
        try {
            return Files.writeString(configFile, toConfigFileContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write the OCI configuration file " + configFile, e);
        }
    }

    @Override
    public String toString() {
        return "OciConfigProfile[" + name + ", user=" + user + ", tenancy=" + tenancy + ", region=" + region + "]";
    }
}

package io.floci.testcontainers.oci;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * Creates OCI SDK authentication providers for tests, signing requests with a locally generated RSA key. Floci OCI
 * parses the signature for tenancy and user context, but never verifies it, so any key works.
 */
public final class TestAuthenticationProviders {

    private static final String USER_ID = "ocid1.user.oc1..testcontainersfloci";
    // Floci OCI only parses the signature, so the fingerprint does not have to match the key
    private static final String FINGERPRINT = "aa:bb:cc:dd:ee:ff:00:11:22:33:44:55:66:77:88:99";

    private TestAuthenticationProviders() {
    }

    /**
     * Returns an authentication provider for the tenancy and region of the given container.
     *
     * @param container the Floci OCI container
     * @return a new authentication provider with a freshly generated RSA key
     */
    public static SimpleAuthenticationDetailsProvider forContainer(FlociOciContainer container) {
        String privateKeyPem = generatePrivateKeyPem();
        return SimpleAuthenticationDetailsProvider.builder()
                .tenantId(container.getTenancyId())
                .userId(USER_ID)
                .fingerprint(FINGERPRINT)
                .privateKeySupplier(() -> new ByteArrayInputStream(privateKeyPem.getBytes(StandardCharsets.UTF_8)))
                .region(Region.fromRegionId(container.getRegion()))
                .build();
    }

    private static String generatePrivateKeyPem() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            return "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8))
                    .encodeToString(keyPair.getPrivate().getEncoded())
                    + "\n-----END PRIVATE KEY-----\n";
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to generate an RSA key for signing OCI requests", e);
        }
    }
}

package io.floci.testcontainers.az.config;

import org.testcontainers.containers.Container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Authentication settings of the Floci Azure server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * AuthConfig config = AuthConfig.builder()
 *     .storageAccountKey("myaccount", "bXktYmFzZTY0LWtleQ==")
 *     .build();
 * }</pre>
 */
public class AuthConfig {

    private static final String DEFAULT_MODE = "dev";

    private final String mode;
    private final Map<String, String> storageAccountKeys;

    private AuthConfig(Builder builder) {
        this.mode = builder.mode;
        this.storageAccountKeys = Collections.unmodifiableMap(new LinkedHashMap<>(builder.storageAccountKeys));
    }

    /**
     * Returns a new {@link Builder} for this configuration.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a new {@link Builder} for this configuration, initialized with the current
     * values of this instance.
     *
     * @return a new builder pre-populated with this configuration's values
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the authentication mode. Floci Azure reserves this setting but does not read it yet:
     * every value, {@code strict} included, behaves like {@code dev}, i.e. any credentials are
     * accepted and SharedKey signatures are not validated.
     *
     * @return the authentication mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Returns the additional storage account keys (account name to base64-encoded key) Floci Azure uses to
     * validate shared-key signed Blob service SAS tokens. Floci Azure always knows the key of the default
     * account {@code devstoreaccount1}; an entry for that account overrides it.
     *
     * @return the storage account keys, never {@code null}
     */
    public Map<String, String> getStorageAccountKeys() {
        return storageAccountKeys;
    }

    /**
     * Applies this authentication configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_AZ_AUTH_MODE", mode);

        storageAccountKeys.forEach((account, key) ->
                container.withEnv(storageAccountKeyEnvVar(account), key));
    }

    private static String storageAccountKeyEnvVar(String account) {
        return "FLOCI_AZ_AUTH_STORAGE_ACCOUNT_KEYS_" + account.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "_");
    }

    /**
     * Builder for {@link AuthConfig}.
     */
    public static class Builder {

        private String mode = DEFAULT_MODE;
        private final Map<String, String> storageAccountKeys = new LinkedHashMap<>();

        private Builder() {
            // Allow instantiation only via AuthConfig.builder()
        }

        private Builder(AuthConfig instance) {
            this.mode = instance.mode;
            this.storageAccountKeys.putAll(instance.storageAccountKeys);
        }

        /**
         * Sets the authentication mode. Floci Azure reserves this setting but does not read it yet:
         * every value, {@code strict} included, behaves like {@code dev}, i.e. any credentials are
         * accepted and SharedKey signatures are not validated. Shared-key signed Blob service SAS
         * tokens are validated against {@link #storageAccountKey(String, String) the storage account
         * keys} independently of this setting.
         *
         * @param mode the authentication mode (default {@value DEFAULT_MODE})
         * @return this builder
         */
        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Adds the key of a storage account, used by Floci Azure to validate shared-key signed Blob service
         * SAS tokens of that account. Storage account names consist of lower-case letters and digits only,
         * which is what Floci Azure can read back from the environment variable
         * {@code FLOCI_AZ_AUTH_STORAGE_ACCOUNT_KEYS_<ACCOUNT>}.
         *
         * @param account the storage account name (e.g. {@code myaccount})
         * @param key     the base64-encoded account key
         * @return this builder
         */
        public Builder storageAccountKey(String account, String key) {
            this.storageAccountKeys.put(account, key);
            return this;
        }

        /**
         * Replaces all storage account keys configured so far.
         *
         * @param storageAccountKeys the storage account keys (account name to base64-encoded key)
         * @return this builder
         * @see #storageAccountKey(String, String)
         */
        public Builder storageAccountKeys(Map<String, String> storageAccountKeys) {
            this.storageAccountKeys.clear();
            this.storageAccountKeys.putAll(storageAccountKeys);
            return this;
        }

        /**
         * Creates an immutable {@link AuthConfig} from this builder.
         *
         * @return the authentication configuration
         */
        public AuthConfig build() {
            return new AuthConfig(this);
        }
    }
}

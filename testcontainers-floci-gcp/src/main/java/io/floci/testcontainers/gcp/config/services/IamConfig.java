package io.floci.testcontainers.gcp.config.services;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;
import io.floci.testcontainers.core.config.services.AbstractServiceConfigBuilder;
import org.testcontainers.containers.Container;

import java.util.Locale;
import java.util.Optional;

/**
 * Configuration for Identity and Access Management (IAM) of Floci GCP.
 *
 * <p>IAM manages service accounts, roles and IAM policies. By default policies are only stored; with
 * {@link AuthorizationMode#ENFORCE} Floci GCP also checks them for incoming requests (currently for Resource
 * Manager projects and Cloud Storage buckets and objects).
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * IamConfig config = IamConfig.builder()
 *     .authorizationMode(IamConfig.AuthorizationMode.ENFORCE)
 *     .bootstrapAdminMember("allAuthenticatedUsers")
 *     .build();
 * }</pre>
 */
public class IamConfig extends AbstractServiceConfig<IamConfig.Builder> {

    private static final AuthorizationMode DEFAULT_AUTHORIZATION_MODE = AuthorizationMode.DISABLED;

    private final AuthorizationMode authorizationMode;
    private final String bootstrapAdminMember;

    private IamConfig(Builder builder) {
        super(builder);
        this.authorizationMode = builder.authorizationMode;
        this.bootstrapAdminMember = builder.bootstrapAdminMember;
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
    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns whether Floci GCP enforces IAM policies for incoming requests.
     *
     * @return the authorization mode
     */
    public AuthorizationMode getAuthorizationMode() {
        return authorizationMode;
    }

    /**
     * Returns the member that is granted {@code roles/storage.admin} in the initial IAM policy of every newly
     * created Cloud Storage bucket, or empty if not set.
     *
     * @return the bootstrap admin member, or empty
     */
    public Optional<String> getBootstrapAdminMember() {
        return Optional.ofNullable(bootstrapAdminMember);
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_SERVICES_IAM_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_GCP_SERVICES_IAM_AUTHORIZATION_MODE",
                    authorizationMode.name().toLowerCase(Locale.ROOT));

            if (bootstrapAdminMember != null) {
                container.withEnv("FLOCI_GCP_SERVICES_IAM_BOOTSTRAP_ADMIN_MEMBER", bootstrapAdminMember);
            }
        }
    }

    /**
     * Whether Floci GCP enforces IAM policies.
     */
    public enum AuthorizationMode {

        /**
         * IAM policies are stored, but not checked; every request is allowed.
         */
        DISABLED,

        /**
         * Requests are checked against the IAM policies of the addressed resources, based on the identity of the
         * caller's credentials.
         */
        ENFORCE
    }

    /**
     * Builder for {@link IamConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, IamConfig> {

        private AuthorizationMode authorizationMode = DEFAULT_AUTHORIZATION_MODE;
        private String bootstrapAdminMember;

        private Builder() {
            // Allow instantiation only via IamConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link IamConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(IamConfig instance) {
            super(instance);
            this.authorizationMode = instance.authorizationMode;
            this.bootstrapAdminMember = instance.bootstrapAdminMember;
        }

        /**
         * Sets whether Floci GCP enforces IAM policies for incoming requests.
         *
         * @param authorizationMode the authorization mode (default {@code DISABLED})
         * @return this builder
         */
        public Builder authorizationMode(AuthorizationMode authorizationMode) {
            this.authorizationMode = authorizationMode;
            return this;
        }

        /**
         * Sets the member that is granted {@code roles/storage.admin} in the initial IAM policy of every newly
         * created Cloud Storage bucket, in addition to the creator. Floci GCP accepts {@code allUsers},
         * {@code allAuthenticatedUsers} or {@code serviceAccount:<email>} and fails to start otherwise.
         *
         * @param bootstrapAdminMember the member, or {@code null} to unset (default unset)
         * @return this builder
         */
        public Builder bootstrapAdminMember(String bootstrapAdminMember) {
            this.bootstrapAdminMember = bootstrapAdminMember;
            return this;
        }

        /**
         * Creates an immutable {@link IamConfig} from this builder.
         *
         * @return the IAM configuration
         */
        @Override
        public IamConfig build() {
            return new IamConfig(this);
        }
    }
}

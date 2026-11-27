package io.floci.testcontainers.gcp.config;

import org.testcontainers.containers.Container;

/**
 * Configuration of the project registry of Floci GCP.
 *
 * <p>By default Floci GCP accepts requests for any project ID. With {@link Builder#strict(boolean) strict} mode,
 * only the default project ({@code FlociGcpContainer#withProjectId(String)}) and projects created through the
 * Resource Manager API exist; requests for any other project are rejected with {@code 403 PERMISSION_DENIED}.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * ProjectsConfig config = ProjectsConfig.builder()
 *     .strict(true)
 *     .build();
 * }</pre>
 */
public class ProjectsConfig {

    private static final boolean DEFAULT_STRICT = false;

    private final boolean strict;

    private ProjectsConfig(Builder builder) {
        this.strict = builder.strict;
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
     * Returns whether only the default project and projects created through Resource Manager exist, so that
     * requests for any other project are rejected with {@code 403 PERMISSION_DENIED}.
     *
     * @return {@code true} if only known projects exist
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Applies this projects configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_GCP_PROJECTS_STRICT", String.valueOf(strict));
    }

    /**
     * Builder for {@link ProjectsConfig}.
     */
    public static class Builder {

        private boolean strict = DEFAULT_STRICT;

        private Builder() {
            // Allow instantiation only via ProjectsConfig.builder()
        }

        private Builder(ProjectsConfig instance) {
            this.strict = instance.strict;
        }

        /**
         * Sets whether only the default project and projects created through Resource Manager
         * ({@code POST /v1/projects}) exist. While on, a REST or gRPC request for any other project – a Resource
         * Manager call, a project IAM policy call or a request to another service – is rejected with
         * {@code 403 PERMISSION_DENIED}, which is how GCP answers for a project the caller cannot see.
         *
         * @param strict {@code true} to accept only known projects (default {@value DEFAULT_STRICT})
         * @return this builder
         */
        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * Creates an immutable {@link ProjectsConfig} from this builder.
         *
         * @return the projects configuration
         */
        public ProjectsConfig build() {
            return new ProjectsConfig(this);
        }
    }
}

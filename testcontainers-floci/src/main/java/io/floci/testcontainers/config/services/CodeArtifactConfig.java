package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * Configuration for CodeArtifact-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * CodeArtifactConfig config = CodeArtifactConfig.builder()
 *     .build();
 * }</pre>
 */
public class CodeArtifactConfig extends AbstractServiceConfig<CodeArtifactConfig.Builder> {

    private static final String DEFAULT_MAVEN_IMAGE = "dzikoysk/reposilite:3.6.3";
    private static final String DEFAULT_NPM_IMAGE = "verdaccio/verdaccio:6.10.4";
    private static final String DEFAULT_PYPI_IMAGE = "pypiserver/pypiserver:v2.4.2";

    private final String mavenUrl;
    private final String mavenToken;
    private final String mavenImage;
    private final String npmImage;
    private final String pypiImage;

    private CodeArtifactConfig(Builder builder) {
        super(builder.enabled);
        this.mavenUrl = builder.mavenUrl;
        this.mavenToken = builder.mavenToken;
        this.mavenImage = builder.mavenImage;
        this.npmImage = builder.npmImage;
        this.pypiImage = builder.pypiImage;
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
     * Returns the URL of an already running Reposilite instance backing the {@code maven} format.
     *
     * <p>When set, Floci uses this URL and skips Reposilite sidecar container management.
     *
     * @return the URL of an already running Reposilite instance backing the {@code maven} format, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getMavenUrl() {
        return Optional.ofNullable(mavenUrl);
    }

    /**
     * Returns the {@code name:secret} access token of the pre-configured Reposilite instance set via the
     * Maven URL.
     *
     * @return the {@code name:secret} access token of the pre-configured Reposilite instance set via the Maven URL, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getMavenToken() {
        return Optional.ofNullable(mavenToken);
    }

    /**
     * Returns the image of the Reposilite sidecar container backing the {@code maven} format.
     *
     * @return the image of the Reposilite sidecar container backing the {@code maven} format
     */
    public String getMavenImage() {
        return mavenImage;
    }

    /**
     * Returns the image of the per-repository Verdaccio container backing the {@code npm} format.
     *
     * @return the image of the per-repository Verdaccio container backing the {@code npm} format
     */
    public String getNpmImage() {
        return npmImage;
    }

    /**
     * Returns the image of the per-repository pypiserver container backing the {@code pypi} format.
     *
     * @return the image of the per-repository pypiserver container backing the {@code pypi} format
     */
    public String getPypiImage() {
        return pypiImage;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_CODEARTIFACT_ENABLED", String.valueOf(isEnabled()));

        if (isEnabled()) {
            if (mavenUrl != null) {
                container.withEnv("FLOCI_SERVICES_CODEARTIFACT_MAVEN_URL", mavenUrl);
            }

            if (mavenToken != null) {
                container.withEnv("FLOCI_SERVICES_CODEARTIFACT_MAVEN_TOKEN", mavenToken);
            }

            container.withEnv("FLOCI_SERVICES_CODEARTIFACT_MAVEN_IMAGE", mavenImage);
            container.withEnv("FLOCI_SERVICES_CODEARTIFACT_NPM_IMAGE", npmImage);
            container.withEnv("FLOCI_SERVICES_CODEARTIFACT_PYPI_IMAGE", pypiImage);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        // Package formats are backed by sidecar containers (Reposilite, Verdaccio, pypiserver)
        return isEnabled();
    }

    /**
     * Builder for {@link CodeArtifactConfig}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, CodeArtifactConfig> {

        private String mavenUrl;
        private String mavenToken;
        private String mavenImage = DEFAULT_MAVEN_IMAGE;
        private String npmImage = DEFAULT_NPM_IMAGE;
        private String pypiImage = DEFAULT_PYPI_IMAGE;

        private Builder() {
            // Allow instantiation only via CodeArtifactConfig.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link CodeArtifactConfig}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(CodeArtifactConfig instance) {
            super(instance);
            this.mavenUrl = instance.getMavenUrl().orElse(null);
            this.mavenToken = instance.getMavenToken().orElse(null);
            this.mavenImage = instance.getMavenImage();
            this.npmImage = instance.getNpmImage();
            this.pypiImage = instance.getPypiImage();
        }

        /**
         * Sets the URL of an already running Reposilite instance backing the {@code maven} format.
         *
         * <p>When set, Floci uses this URL and skips Reposilite sidecar container management.
         *
         * @param mavenUrl the URL of an already running Reposilite instance backing the {@code maven} format, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder mavenUrl(String mavenUrl) {
            this.mavenUrl = mavenUrl;
            return this;
        }

        /**
         * Sets the {@code name:secret} access token of the pre-configured Reposilite instance set via the
         * Maven URL.
         *
         * @param mavenToken the {@code name:secret} access token of the pre-configured Reposilite instance set via the Maven URL, or {@code null} to use Floci's default
         * @return this builder
         */
        public Builder mavenToken(String mavenToken) {
            this.mavenToken = mavenToken;
            return this;
        }

        /**
         * Sets the image of the Reposilite sidecar container backing the {@code maven} format.
         *
         * @param mavenImage the image of the Reposilite sidecar container backing the {@code maven} format (default {@value DEFAULT_MAVEN_IMAGE})
         * @return this builder
         */
        public Builder mavenImage(String mavenImage) {
            this.mavenImage = mavenImage;
            return this;
        }

        /**
         * Sets the image of the per-repository Verdaccio container backing the {@code npm} format.
         *
         * @param npmImage the image of the per-repository Verdaccio container backing the {@code npm} format (default {@value DEFAULT_NPM_IMAGE})
         * @return this builder
         */
        public Builder npmImage(String npmImage) {
            this.npmImage = npmImage;
            return this;
        }

        /**
         * Sets the image of the per-repository pypiserver container backing the {@code pypi} format.
         *
         * @param pypiImage the image of the per-repository pypiserver container backing the {@code pypi} format (default {@value DEFAULT_PYPI_IMAGE})
         * @return this builder
         */
        public Builder pypiImage(String pypiImage) {
            this.pypiImage = pypiImage;
            return this;
        }

        /**
         * Creates an immutable {@link CodeArtifactConfig} from this builder.
         *
         * @return the CodeArtifact configuration
         */
        @Override
        public CodeArtifactConfig build() {
            return new CodeArtifactConfig(this);
        }
    }
}

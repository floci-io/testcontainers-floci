package io.floci.testcontainers.az.config;

import java.util.Objects;
import java.util.Optional;

/**
 * One tenant of the Floci Azure directory, see {@link DirectoryConfig}. Only the id is required; the
 * other properties fall back to Floci Azure's defaults.
 *
 * <p>Instances are created via {@link DirectoryConfig.Builder}, e.g.:
 * <pre>{@code
 * DirectoryConfig config = DirectoryConfig.builder()
 *     .tenant("11111111-0000-0000-0000-000000000000", t -> t
 *         .displayName("Contoso Gov")
 *         .defaultDomain("contoso.example")
 *         .cloud("AzureUSGovernment"))
 *     .build();
 * }</pre>
 */
public final class TenantConfig {

    private final String id;
    private final String displayName;
    private final String defaultDomain;
    private final String cloud;

    private TenantConfig(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.defaultDomain = builder.defaultDomain;
        this.cloud = builder.cloud;
    }

    static Builder builder(String id) {
        return new Builder(id);
    }

    /**
     * Returns the tenant id.
     *
     * @return the tenant id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the display name of the tenant. When empty, Floci Azure uses {@code floci-az local}.
     *
     * @return the display name, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns the default domain of the tenant. When empty, Floci Azure uses {@code floci-az.local}.
     *
     * @return the default domain, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getDefaultDomain() {
        return Optional.ofNullable(defaultDomain);
    }

    /**
     * Returns the cloud the tenant lives in ({@code AzureCloud}, {@code AzureUSGovernment} or
     * {@code AzureChinaCloud}). When empty, the tenant lives in the {@linkplain DirectoryConfig#getDefaultCloud()
     * default cloud}.
     *
     * @return the cloud name, or {@link Optional#empty()} if not configured
     */
    public Optional<String> getCloud() {
        return Optional.ofNullable(cloud);
    }

    /**
     * Builder for {@link TenantConfig}.
     */
    public static final class Builder {

        private final String id;
        private String displayName;
        private String defaultDomain;
        private String cloud;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        /**
         * Sets the display name of the tenant. When not set, Floci Azure uses {@code floci-az local}.
         *
         * @param displayName the display name ({@code null} to unset)
         * @return this builder
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the default domain of the tenant. When not set, Floci Azure uses {@code floci-az.local}.
         *
         * @param defaultDomain the default domain ({@code null} to unset)
         * @return this builder
         */
        public Builder defaultDomain(String defaultDomain) {
            this.defaultDomain = defaultDomain;
            return this;
        }

        /**
         * Sets the cloud the tenant lives in: {@code AzureCloud}, {@code AzureUSGovernment} or
         * {@code AzureChinaCloud}. An unknown name makes Floci Azure fail at startup. When not set, the
         * tenant lives in the {@linkplain DirectoryConfig.Builder#defaultCloud(String) default cloud}.
         *
         * @param cloud the cloud name ({@code null} to unset)
         * @return this builder
         */
        public Builder cloud(String cloud) {
            this.cloud = cloud;
            return this;
        }

        TenantConfig build() {
            return new TenantConfig(this);
        }
    }
}

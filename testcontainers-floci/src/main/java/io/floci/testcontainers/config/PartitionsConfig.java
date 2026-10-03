package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

import java.util.Optional;

/**
 * AWS partition configuration for the Floci server: which partition the deployment serves and how
 * strictly requests are checked against it.
 *
 * <p>The partition is normally derived from the default region ({@code cn-north-1} means
 * {@code aws-cn}); {@link Builder#id(String)} pins it explicitly.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * PartitionsConfig config = PartitionsConfig.builder()
 *     .id("aws-cn")
 *     .strict(true)
 *     .build();
 * }</pre>
 */
public class PartitionsConfig {

    private static final boolean DEFAULT_STRICT = false;
    private static final boolean DEFAULT_ALLOW_UNKNOWN_REGIONS = false;

    private final String id;
    private final boolean strict;
    private final boolean allowUnknownRegions;

    private PartitionsConfig(Builder builder) {
        this.id = builder.id;
        this.strict = builder.strict;
        this.allowUnknownRegions = builder.allowUnknownRegions;
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
     * Returns the explicitly pinned AWS partition (e.g. {@code aws}, {@code aws-cn}, {@code aws-us-gov}), or
     * {@link Optional#empty()} if Floci derives it from the default region.
     *
     * @return the partition id, or {@link Optional#empty()}
     */
    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    /**
     * Returns whether requests signed for a service AWS does not publish in the request's partition
     * (e.g. CloudFront in GovCloud) are refused.
     *
     * @return {@code true} if such requests are refused
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Returns whether a request whose SigV4 credential scope names a region that no partition publishes
     * or admits by its region pattern is accepted.
     *
     * @return {@code true} if requests for unknown regions are accepted
     */
    public boolean isAllowUnknownRegions() {
        return allowUnknownRegions;
    }

    /**
     * Applies this partitions configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        if (id != null) {
            container.withEnv("FLOCI_PARTITIONS_ID", id);
        }
        container.withEnv("FLOCI_PARTITIONS_STRICT", String.valueOf(strict));
        container.withEnv("FLOCI_PARTITIONS_ALLOW_UNKNOWN_REGIONS", String.valueOf(allowUnknownRegions));
    }

    /**
     * Builder for {@link PartitionsConfig}.
     */
    public static class Builder {

        private String id = null;
        private boolean strict = DEFAULT_STRICT;
        private boolean allowUnknownRegions = DEFAULT_ALLOW_UNKNOWN_REGIONS;

        private Builder() {
            // Allow instantiation only via PartitionsConfig.builder()
        }

        private Builder(PartitionsConfig instance) {
            this.id = instance.id;
            this.strict = instance.strict;
            this.allowUnknownRegions = instance.allowUnknownRegions;
        }

        /**
         * Pins the AWS partition the deployment serves (e.g. {@code aws}, {@code aws-cn},
         * {@code aws-us-gov}). When unset, Floci derives the partition from the default region
         * ({@code cn-north-1} means {@code aws-cn}). Floci refuses to start with a value that names
         * no partition or contradicts the default region.
         *
         * @param id the partition id, or {@code null} to derive it from the default region (default)
         * @return this builder
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets whether requests signed for a service AWS does not publish in the request's partition
         * (CloudFront in GovCloud, IAM in {@code aws-eusc}) are refused. On AWS such a request never
         * reaches an API because its endpoint does not resolve; by default Floci serves every enabled
         * service in every partition.
         *
         * @param strict {@code true} to refuse such requests (default {@value DEFAULT_STRICT})
         * @return this builder
         */
        public Builder strict(boolean strict) {
            this.strict = strict;
            return this;
        }

        /**
         * Sets whether a request whose SigV4 credential scope names a region that no partition publishes
         * or admits by its region pattern (e.g. {@code polygondwanaland-west-1}) is accepted. Refused by
         * default, because on AWS such a request never resolves a host; enable it to give every region
         * label its own region namespace, as Floci did before.
         *
         * @param allowUnknownRegions {@code true} to accept requests for unknown regions
         *                            (default {@value DEFAULT_ALLOW_UNKNOWN_REGIONS})
         * @return this builder
         */
        public Builder allowUnknownRegions(boolean allowUnknownRegions) {
            this.allowUnknownRegions = allowUnknownRegions;
            return this;
        }

        /**
         * Creates an immutable {@link PartitionsConfig} from this builder.
         *
         * @return the partitions configuration
         */
        public PartitionsConfig build() {
            return new PartitionsConfig(this);
        }
    }
}

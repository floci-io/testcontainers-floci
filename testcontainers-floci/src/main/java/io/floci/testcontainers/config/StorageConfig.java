package io.floci.testcontainers.config;

import org.testcontainers.containers.Container;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Storage-related configuration for the Floci server.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * StorageConfig config = StorageConfig.builder()
 *     .hostPersistentPath(Path.of("/tmp/floci-data"))
 *     .pruneVolumesOnDelete(true)
 *     .build();
 * }</pre>
 */
public class StorageConfig {

    private static final boolean DEFAULT_PRUNE_VOLUMES_ON_DELETE = true;

    private final Path hostPersistentPath;
    private final boolean pruneVolumesOnDelete;

    private StorageConfig(Builder builder) {
        this.hostPersistentPath = builder.hostPersistentPath;
        this.pruneVolumesOnDelete = builder.pruneVolumesOnDelete;
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
     * Returns the host path that is bind-mounted (read-write) into the Floci container
     * at {@code /app/data} for persistent storage.
     *
     * <p>When present, the path is also exported as the
     * {@code FLOCI_STORAGE_HOST_PERSISTENT_PATH} environment variable so Floci knows
     * where to place state files. When absent, no persistent volume is mounted and
     * Floci uses ephemeral in-container storage.
     *
     * @return the host-side persistent storage path, or {@link Optional#empty()} if not configured
     */
    public Optional<Path> getHostPersistentPath() {
        return Optional.ofNullable(hostPersistentPath);
    }

    /**
     * Returns whether named volumes are removed immediately after a child container stops
     * on resource delete.
     *
     * <p>When {@code true}, named volumes are removed immediately after a child container stops
     * on resource delete. In {@code memory} storage mode volumes are always removed regardless
     * of this flag. Defaults to {@code false} to match real AWS behaviour (data survives delete).
     *
     * @return {@code true} if volumes are pruned on delete
     */
    public boolean isPruneVolumesOnDelete() {
        return pruneVolumesOnDelete;
    }

    /**
     * Applies this storage configuration to the given container by setting
     * the appropriate environment variables.
     *
     * @param container the container to configure
     */
    public void applyEnvVarsToContainer(Container<?> container) {
        if (hostPersistentPath != null) {
            container.withEnv("FLOCI_STORAGE_HOST_PERSISTENT_PATH", hostPersistentPath.toString());
        }
        container.withEnv("FLOCI_STORAGE_PRUNE_VOLUMES_ON_DELETE", String.valueOf(pruneVolumesOnDelete));
    }

    /**
     * Builder for {@link StorageConfig}.
     */
    public static class Builder {

        private Path hostPersistentPath = null;
        private boolean pruneVolumesOnDelete = DEFAULT_PRUNE_VOLUMES_ON_DELETE;

        private Builder() {
            // Allow instantiation only via StorageConfig.builder()
        }

        private Builder(StorageConfig instance) {
            this.hostPersistentPath = instance.hostPersistentPath;
            this.pruneVolumesOnDelete = instance.pruneVolumesOnDelete;
        }

        /**
         * Generates a temporary directory on the host and uses it as the persistent
         * storage path. The directory is created under the JVM's default temp-file
         * location with the prefix {@code floci-} and is bind-mounted into the container
         * at {@code /app/data}.
         *
         * @return this builder
         * @throws IllegalStateException if the temporary directory cannot be created
         */
        public Builder randomHostPersistentPath() {
            try {
                this.hostPersistentPath = Files.createTempDirectory("floci-").toAbsolutePath();
                return this;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to create temporary persistent storage directory", e);
            }
        }

        /**
         * Sets whether named volumes are removed immediately after a child container stops
         * on resource delete. In {@code memory} storage mode volumes are always removed regardless
         * of this flag. Defaults to {@code false} to match real AWS behaviour (data survives delete).
         *
         * @param pruneVolumesOnDelete {@code true} to prune volumes on delete (default {@value DEFAULT_PRUNE_VOLUMES_ON_DELETE})
         * @return this builder
         */
        public Builder pruneVolumesOnDelete(boolean pruneVolumesOnDelete) {
            this.pruneVolumesOnDelete = pruneVolumesOnDelete;
            return this;
        }

        /**
         * Creates an immutable {@link StorageConfig} from this builder.
         *
         * @return the storage configuration
         */
        public StorageConfig build() {
            return new StorageConfig(this);
        }
    }
}

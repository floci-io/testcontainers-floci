package org.testcontainers.containers;

import org.testcontainers.images.builder.Transferable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * Test-only bridge to {@link GenericContainer}'s package-private
 * {@link GenericContainer#getCopyToTransferableContainerPathMap()}, which holds the in-memory files
 * queued for copy into the container via {@code withCopyToContainer(Transferable, String)}.
 *
 * <p>Lives in {@code org.testcontainers.containers} so it can call that package-private accessor
 * without reflection; keep it under {@code src/test}.
 */
public final class TransferableCopyInspector {

    private TransferableCopyInspector() {
    }

    /**
     * Returns the map of pending in-memory copies (content to container path) for the given container.
     */
    public static Map<Transferable, String> pendingCopies(GenericContainer<?> container) {
        return container.getCopyToTransferableContainerPathMap();
    }

    /**
     * Returns the UTF-8 content queued for copy to the given container path, if any.
     */
    public static Optional<String> contentCopiedTo(GenericContainer<?> container, String containerPath) {
        return pendingCopies(container).entrySet().stream()
                .filter(entry -> containerPath.equals(entry.getValue()))
                .map(entry -> new String(entry.getKey().getBytes(), StandardCharsets.UTF_8))
                .findFirst();
    }
}

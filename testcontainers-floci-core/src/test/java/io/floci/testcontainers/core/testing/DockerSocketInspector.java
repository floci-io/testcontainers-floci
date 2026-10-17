package io.floci.testcontainers.core.testing;

import io.floci.testcontainers.core.config.services.AbstractServiceConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Lists the service configurations of a Floci container that currently require the Docker socket, so each cloud
 * module can compare them with the reference list of the specification.
 */
public final class DockerSocketInspector {

    private DockerSocketInspector() {
    }

    /**
     * Returns the simple class names of all service configurations exposed by the container's public, parameterless
     * {@code get…Config()} methods.
     *
     * @param container the container to inspect
     * @return the sorted class names of all service configurations
     */
    public static Set<String> serviceConfigs(Object container) {
        return Arrays.stream(serviceConfigGetters(container))
                .map(getter -> invoke(getter, container).getClass().getSimpleName())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Returns the simple class names of the service configurations whose
     * {@link AbstractServiceConfig#requiresDockerSocket()} is currently {@code true}.
     *
     * @param container the container to inspect
     * @return the sorted class names of the service configurations requiring the Docker socket
     */
    public static Set<String> configsRequiringDockerSocket(Object container) {
        return Arrays.stream(serviceConfigGetters(container))
                .map(getter -> invoke(getter, container))
                .filter(AbstractServiceConfig::requiresDockerSocket)
                .map(config -> config.getClass().getSimpleName())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static Method[] serviceConfigGetters(Object container) {
        return Arrays.stream(container.getClass().getMethods())
                .filter(m -> m.getParameterCount() == 0 && !Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getName().startsWith("get") && m.getName().endsWith("Config"))
                .filter(m -> AbstractServiceConfig.class.isAssignableFrom(m.getReturnType()))
                .toArray(Method[]::new);
    }

    private static AbstractServiceConfig<?> invoke(Method getter, Object container) {
        try {
            return (AbstractServiceConfig<?>) getter.invoke(container);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to call " + getter, e);
        }
    }
}

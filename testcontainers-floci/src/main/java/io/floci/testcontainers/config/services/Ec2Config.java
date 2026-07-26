package io.floci.testcontainers.config.services;

import org.testcontainers.containers.Container;

/**
 * Configuration for EC2-specific container settings.
 *
 * <p>Instances are created via {@link Builder}:
 * <pre>{@code
 * Ec2Config config = Ec2Config.builder()
 *     .appPortRange(30000, 10)
 *     .build();
 * }</pre>
 */
public class Ec2Config extends AbstractServiceConfig<Ec2Config.Builder> {

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_IMDS_PORT = 9169;
    private static final int DEFAULT_SSH_PORT_RANGE_START = 2200;
    private static final int DEFAULT_SSH_PORT_RANGE_END = 2299;
    private static final boolean DEFAULT_PUBLISH_SECURITY_GROUP_PORTS = true;
    private static final int DEFAULT_APP_PORT_RANGE_START = 30000;
    private static final int DEFAULT_APP_PORTS_COUNT = 10;
    private static final int DEFAULT_MAX_PUBLISHED_PORTS_PER_INSTANCE = 2;
    private static final String DEFAULT_SOCAT_IMAGE = "alpine/socat";

    private final boolean mock;
    private final int imdsPort;
    private final int sshPortRangeStart;
    private final int sshPortRangeEnd;
    private final boolean publishSecurityGroupPorts;
    private final int appPortRangeStart;
    private final int appPortsCount;
    private final int maxPublishedPortsPerInstance;
    private final String socatImage;
    private final AutoScaling autoScaling;

    private Ec2Config(Builder builder) {
        super(builder.enabled);
        this.mock = builder.mock;
        this.imdsPort = builder.imdsPort;
        this.sshPortRangeStart = builder.sshPortRangeStart;
        this.sshPortRangeEnd = builder.sshPortRangeEnd;
        this.publishSecurityGroupPorts = builder.publishSecurityGroupPorts;
        this.appPortRangeStart = builder.appPortRangeStart;
        this.appPortsCount = builder.appPortsCount;
        this.maxPublishedPortsPerInstance = builder.maxPublishedPortsPerInstance;
        this.socatImage = builder.socatImage;
        this.autoScaling = builder.autoScaling;
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
     * Returns whether EC2 instances go straight to RUNNING without launching Docker containers.
     *
     * @return {@code true} if mock mode is enabled
     */
    public boolean isMock() {
        return mock;
    }

    /**
     * Returns the port on the Floci host for the IMDS HTTP server (169.254.169.254 equivalent).
     *
     * @return the IMDS port
     */
    public int getImdsPort() {
        return imdsPort;
    }

    /**
     * Returns the lowest host port in the range published for EC2 instance SSH (port 22).
     *
     * @return the SSH port range start
     */
    public int getSshPortRangeStart() {
        return sshPortRangeStart;
    }

    /**
     * Returns the highest host port in the range published for EC2 instance SSH (port 22).
     *
     * @return the SSH port range end
     */
    public int getSshPortRangeEnd() {
        return sshPortRangeEnd;
    }

    /**
     * Returns whether TCP ports opened by an instance's security-group ingress rules are
     * published on the host via a socat sidecar container, both at launch and on later
     * authorize-security-group-ingress. When {@code false}, security groups are metadata only.
     *
     * @return {@code true} if published security-group ports are enabled
     */
    public boolean isPublishSecurityGroupPorts() {
        return publishSecurityGroupPorts;
    }

    /**
     * Returns the lowest host port in the range allocated for published security-group app ports.
     *
     * @return the app port range start
     */
    public int getAppPortRangeStart() {
        return appPortRangeStart;
    }

    /**
     * Returns the number of ports allocated for published security-group app ports, starting
     * from {@link #getAppPortRangeStart()}.
     *
     * @return the number of app ports
     */
    public int getAppPortsCount() {
        return appPortsCount;
    }

    /**
     * Returns the highest host port in the range allocated for published security-group app ports.
     *
     * @return the app port range end
     */
    public int getAppPortRangeEnd() {
        return appPortRangeStart + appPortsCount - 1;
    }

    /**
     * Returns the upper bound on app ports published per instance. Also bounds any single
     * ingress rule's port span: wider ranges (e.g. an allow-all 0-65535 rule) are skipped so a
     * single rule cannot spawn thousands of socat sidecars or exhaust the host-port range.
     *
     * @return the maximum published ports per instance
     */
    public int getMaxPublishedPortsPerInstance() {
        return maxPublishedPortsPerInstance;
    }

    /**
     * Returns the image used for the socat sidecar that forwards published security-group ports.
     *
     * @return the socat image name
     */
    public String getSocatImage() {
        return socatImage;
    }

    /**
     * Returns the Auto Scaling configuration.
     *
     * @return the Auto Scaling configuration
     */
    public AutoScaling getAutoScaling() {
        return autoScaling;
    }

    @Override
    public void applyEnvVarsToContainer(Container<?> container) {
        container.withEnv("FLOCI_SERVICES_EC2_ENABLED", String.valueOf(isEnabled()));
        container.withEnv("FLOCI_SERVICES_AUTOSCALING_ENABLED", String.valueOf(autoScaling.enabled()));

        if (isEnabled()) {
            container.withEnv("FLOCI_SERVICES_EC2_MOCK", String.valueOf(mock));
            container.withEnv("FLOCI_SERVICES_EC2_IMDS_PORT", String.valueOf(imdsPort));
            container.withEnv("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_START", String.valueOf(sshPortRangeStart));
            container.withEnv("FLOCI_SERVICES_EC2_SSH_PORT_RANGE_END", String.valueOf(sshPortRangeEnd));
            container.withEnv("FLOCI_SERVICES_EC2_PUBLISH_SECURITY_GROUP_PORTS", String.valueOf(publishSecurityGroupPorts));
            container.withEnv("FLOCI_SERVICES_EC2_APP_PORT_RANGE_START", String.valueOf(appPortRangeStart));
            container.withEnv("FLOCI_SERVICES_EC2_APP_PORT_RANGE_END", String.valueOf(getAppPortRangeEnd()));
            container.withEnv("FLOCI_SERVICES_EC2_MAX_PUBLISHED_PORTS_PER_INSTANCE", String.valueOf(maxPublishedPortsPerInstance));
            container.withEnv("FLOCI_SERVICES_EC2_SOCAT_IMAGE", socatImage);
        }
    }

    @Override
    public void applyExposedPortsToContainer(Container<?> container) {
        if (isEnabled()) {
            container.addExposedPorts(imdsPort);

            if (publishSecurityGroupPorts) {
                for (int port = appPortRangeStart; port <= getAppPortRangeEnd(); port++) {
                    container.addExposedPorts(port);
                }
            }
        }
    }

    /**
     * Builder for {@link Ec2Config}.
     */
    public static class Builder extends AbstractServiceConfigBuilder<Builder, Ec2Config> {

        private boolean mock = DEFAULT_MOCK;
        private int imdsPort = DEFAULT_IMDS_PORT;
        private int sshPortRangeStart = DEFAULT_SSH_PORT_RANGE_START;
        private int sshPortRangeEnd = DEFAULT_SSH_PORT_RANGE_END;
        private boolean publishSecurityGroupPorts = DEFAULT_PUBLISH_SECURITY_GROUP_PORTS;
        private int appPortRangeStart = DEFAULT_APP_PORT_RANGE_START;
        private int appPortsCount = DEFAULT_APP_PORTS_COUNT;
        private int maxPublishedPortsPerInstance = DEFAULT_MAX_PUBLISHED_PORTS_PER_INSTANCE;
        private String socatImage = DEFAULT_SOCAT_IMAGE;
        private AutoScaling autoScaling = new DefaultAutoScaling(true);

        private Builder() {
            // Allow instantiation only via Ec2Config.builder()
        }

        /**
         * Creates a new builder initialized with the values of the given {@link Ec2Config}.
         *
         * @param instance the configuration instance to copy values from
         */
        private Builder(Ec2Config instance) {
            super(instance);
            this.mock = instance.isMock();
            this.imdsPort = instance.getImdsPort();
            this.sshPortRangeStart = instance.getSshPortRangeStart();
            this.sshPortRangeEnd = instance.getSshPortRangeEnd();
            this.publishSecurityGroupPorts = instance.isPublishSecurityGroupPorts();
            this.appPortRangeStart = instance.getAppPortRangeStart();
            this.appPortsCount = instance.getAppPortsCount();
            this.maxPublishedPortsPerInstance = instance.getMaxPublishedPortsPerInstance();
            this.socatImage = instance.getSocatImage();
            this.autoScaling = instance.getAutoScaling();
        }

        /**
         * Sets whether EC2 instances go straight to RUNNING without launching Docker containers.
         *
         * @param mock {@code true} to enable mock mode (default {@value DEFAULT_MOCK})
         * @return this builder
         */
        public Builder mock(boolean mock) {
            this.mock = mock;
            return this;
        }

        /**
         * Sets the port on the Floci host for the IMDS HTTP server (169.254.169.254 equivalent).
         *
         * @param imdsPort the IMDS port (default {@value DEFAULT_IMDS_PORT})
         * @return this builder
         */
        public Builder imdsPort(int imdsPort) {
            this.imdsPort = imdsPort;
            return this;
        }

        /**
         * Sets the SSH port range for EC2 instance access.
         *
         * @param start the lowest host port for EC2 instance SSH (default {@value DEFAULT_SSH_PORT_RANGE_START})
         * @param end   the highest host port for EC2 instance SSH (default {@value DEFAULT_SSH_PORT_RANGE_END})
         * @return this builder
         */
        public Builder sshPortRange(int start, int end) {
            this.sshPortRangeStart = start;
            this.sshPortRangeEnd = end;
            return this;
        }

        /**
         * Sets whether TCP ports opened by an instance's security-group ingress rules are
         * published on the host via a socat sidecar container, both at launch and on later
         * authorize-security-group-ingress.
         *
         * @param publishSecurityGroupPorts {@code true} to publish security-group ports
         *                                   (default {@value DEFAULT_PUBLISH_SECURITY_GROUP_PORTS})
         * @return this builder
         */
        public Builder publishSecurityGroupPorts(boolean publishSecurityGroupPorts) {
            this.publishSecurityGroupPorts = publishSecurityGroupPorts;
            return this;
        }

        /**
         * Sets the port range allocated for published security-group app ports.
         *
         * @param start the lowest host port in the range (default {@value DEFAULT_APP_PORT_RANGE_START})
         * @param count the number of ports in the range (default {@value DEFAULT_APP_PORTS_COUNT})
         * @return this builder
         */
        public Builder appPortRange(int start, int count) {
            this.appPortRangeStart = start;
            this.appPortsCount = count;
            return this;
        }

        /**
         * Sets the upper bound on app ports published per instance. Also bounds any single
         * ingress rule's port span: wider ranges (e.g. an allow-all 0-65535 rule) are skipped so
         * a single rule cannot spawn thousands of socat sidecars or exhaust the host-port range.
         *
         * @param maxPublishedPortsPerInstance the maximum published ports per instance
         *                                     (default {@value DEFAULT_MAX_PUBLISHED_PORTS_PER_INSTANCE})
         * @return this builder
         */
        public Builder maxPublishedPortsPerInstance(int maxPublishedPortsPerInstance) {
            this.maxPublishedPortsPerInstance = maxPublishedPortsPerInstance;
            return this;
        }

        /**
         * Sets the image used for the socat sidecar that forwards published security-group ports.
         *
         * @param socatImage the socat image name (default {@value DEFAULT_SOCAT_IMAGE})
         * @return this builder
         */
        public Builder socatImage(String socatImage) {
            this.socatImage = socatImage;
            return this;
        }

        /**
         * Sets whether Auto Scaling is enabled.
         *
         * @param enabled {@code true} to enable Auto Scaling (default {@code true})
         * @return this builder
         */
        public Builder autoScaling(boolean enabled) {
            this.autoScaling = new DefaultAutoScaling(enabled);
            return this;
        }

        /**
         * Creates an immutable {@link Ec2Config} from this builder.
         *
         * @return the EC2 configuration
         */
        public Ec2Config build() {
            return new Ec2Config(this);
        }
    }

    /**
     * Configuration for EC2 Auto Scaling.
     */
    public interface AutoScaling {
        /**
         * Returns whether Auto Scaling is enabled.
         *
         * @return {@code true} if Auto Scaling is enabled
         */
        boolean enabled();
    }

    /**
     * Default implementation of {@link AutoScaling}.
     */
    private record DefaultAutoScaling(boolean enabled) implements AutoScaling {
    }
}

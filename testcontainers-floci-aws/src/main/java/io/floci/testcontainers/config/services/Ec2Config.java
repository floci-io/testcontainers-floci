package io.floci.testcontainers.config.services;

import java.util.Optional;
import java.util.UUID;

import org.testcontainers.containers.Container;

import java.util.Locale;
import org.testcontainers.images.builder.Transferable;

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

    private static final String IMAGE_CATALOG_FILE_PREFIX = "/tmp/floci-ec2-image-catalog-";
    private static final String IMAGE_CATALOG_FILE_SUFFIX = ".yaml";

    private static final boolean DEFAULT_MOCK = false;
    private static final int DEFAULT_IMDS_PORT = 9169;
    private static final int DEFAULT_SSH_PORT_RANGE_START = 2200;
    private static final int DEFAULT_SSH_PORT_RANGE_END = 2299;
    private static final boolean DEFAULT_PUBLISH_SECURITY_GROUP_PORTS = true;
    private static final int DEFAULT_APP_PORT_RANGE_START = 30000;
    private static final int DEFAULT_APP_PORTS_COUNT = 10;
    private static final int DEFAULT_MAX_PUBLISHED_PORTS_PER_INSTANCE = 2;
    private static final String DEFAULT_SOCAT_IMAGE = "alpine/socat";
    private static final boolean DEFAULT_AWS_FAITHFUL_PRIVATE_IP = false;
    private static final boolean DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP = true;
    private static final boolean DEFAULT_VOLUME_BLOCK_DEVICES = true;
    private static final String DEFAULT_VOLUME_HELPER_IMAGE = "alpine:3.21";
    private static final boolean DEFAULT_INSTANCE_RESOURCE_LIMITS = true;
    private static final boolean DEFAULT_VPC_NETWORKS_ENABLED = true;
    private static final String DEFAULT_VPC_NETWORKS_FALLBACK_POOL = "10.240.0.0/12";
    private static final int DEFAULT_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH = 16;
    private static final boolean DEFAULT_VPC_NETWORKS_RECONCILE_ON_STARTUP = true;
    private static final String DEFAULT_VPC_NETWORKS_DRIVER = "bridge";
    private static final boolean DEFAULT_ENFORCE_VCPU_QUOTAS = false;
    private static final boolean DEFAULT_PUBLIC_IP_FROM_SERVICE_NETWORK = false;
    private static final MockPublicAddresses DEFAULT_MOCK_PUBLIC_ADDRESSES = MockPublicAddresses.NONE;

    private final boolean mock;
    private final int imdsPort;
    private final int sshPortRangeStart;
    private final int sshPortRangeEnd;
    private final boolean publishSecurityGroupPorts;
    private final int appPortRangeStart;
    private final int appPortsCount;
    private final int maxPublishedPortsPerInstance;
    private final String socatImage;
    private final boolean awsFaithfulPrivateIp;
    private final Boolean containerIpsRoutable;
    private final AutoScaling autoScaling;
    private final String imageCatalogFile;
    private final String imageCatalog;
    private final boolean reconcileContainersOnStartup;
    private final boolean volumeBlockDevices;
    private final String volumeHelperImage;
    private final boolean instanceResourceLimits;
    private final boolean vpcNetworksEnabled;
    private final String vpcNetworksFallbackPool;
    private final int vpcNetworksFallbackPrefixLength;
    private final boolean vpcNetworksReconcileOnStartup;
    private final String vpcNetworksDriver;
    private final String managedPrefixListCatalogPath;
    private final boolean enforceVcpuQuotas;
    private final boolean publicIpFromServiceNetwork;
    private final MockPublicAddresses mockPublicAddresses;
    private final String mockPublicAddress;

    private Ec2Config(Builder builder) {
        super(builder);
        this.mock = builder.mock;
        this.imdsPort = builder.imdsPort;
        this.sshPortRangeStart = builder.sshPortRangeStart;
        this.sshPortRangeEnd = builder.sshPortRangeEnd;
        this.publishSecurityGroupPorts = builder.publishSecurityGroupPorts;
        this.appPortRangeStart = builder.appPortRangeStart;
        this.appPortsCount = builder.appPortsCount;
        this.maxPublishedPortsPerInstance = builder.maxPublishedPortsPerInstance;
        this.socatImage = builder.socatImage;
        this.awsFaithfulPrivateIp = builder.awsFaithfulPrivateIp;
        this.containerIpsRoutable = builder.containerIpsRoutable;
        this.autoScaling = builder.autoScaling;
        this.imageCatalogFile = builder.imageCatalogFile;
        this.imageCatalog = builder.imageCatalog;
        this.reconcileContainersOnStartup = builder.reconcileContainersOnStartup;
        this.volumeBlockDevices = builder.volumeBlockDevices;
        this.volumeHelperImage = builder.volumeHelperImage;
        this.instanceResourceLimits = builder.instanceResourceLimits;
        this.vpcNetworksEnabled = builder.vpcNetworksEnabled;
        this.vpcNetworksFallbackPool = builder.vpcNetworksFallbackPool;
        this.vpcNetworksFallbackPrefixLength = builder.vpcNetworksFallbackPrefixLength;
        this.vpcNetworksReconcileOnStartup = builder.vpcNetworksReconcileOnStartup;
        this.vpcNetworksDriver = builder.vpcNetworksDriver;
        this.managedPrefixListCatalogPath = builder.managedPrefixListCatalogPath;
        this.enforceVcpuQuotas = builder.enforceVcpuQuotas;
        this.publicIpFromServiceNetwork = builder.publicIpFromServiceNetwork;
        this.mockPublicAddresses = builder.mockPublicAddresses;
        this.mockPublicAddress = builder.mockPublicAddress;
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
     * Returns whether DescribeInstances and IMDS report each instance's CFN- and subnet-allocated
     * private IP (AWS-faithful) instead of the Docker container's bridge IP (#1983). Default
     * {@code false} keeps the bridge IP as the reported private address, which lets instances
     * reach each other at that address on the shared Docker network. Routing/IMDS always use the
     * container bridge IP regardless of this flag; only the reported {@code PrivateIpAddress}
     * changes.
     *
     * @return {@code true} if the AWS-faithful private IP is reported
     */
    public boolean isAwsFaithfulPrivateIp() {
        return awsFaithfulPrivateIp;
    }

    /**
     * Returns whether an EC2 instance's Docker container IP is routable from the machines that
     * consume Floci's API responses (Terraform, Terratest, your shell). When {@code true},
     * DescribeInstances / DescribeAddresses report the container IP, so the address they hand out
     * accepts connections on the service's real port (22 for SSH, and every other port the guest
     * listens on) with no port mapping involved. When {@code false}, Floci keeps reporting
     * {@code 127.0.0.1} and reachability depends on the published high host ports.
     *
     * <p>{@link Optional#empty()} (the default) means auto-detect: Floci opens a throwaway TCP
     * connection towards the container network and treats a refusal as proof of a route. Set it
     * explicitly when the probe cannot speak for your clients — most notably when Floci itself runs
     * as a container, where the probe measures container-to-container reachability rather than
     * host-to-container.
     *
     * <p>Env var: {@code FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE}
     *
     * @return whether container IPs are routable, or {@link Optional#empty()} to auto-detect
     */
    public Optional<Boolean> getContainerIpsRoutable() {
        return Optional.ofNullable(containerIpsRoutable);
    }

    /**
     * Returns the Auto Scaling configuration.
     *
     * @return the Auto Scaling configuration
     */
    public AutoScaling getAutoScaling() {
        return autoScaling;
    }

    /**
     * Returns whether Floci removes, on startup, EC2 instance containers left on the Docker daemon by a
     * previous run of the same Floci whose instance record did not survive the restart.
     *
     * <p>Stopped instances are never swept, their containers are exactly what StartInstances revives.
     *
     * @return whether Floci removes, on startup, EC2 instance containers left on the Docker daemon by a previous run of the same Floci whose instance record did not survive the restart
     */
    public boolean isReconcileContainersOnStartup() {
        return reconcileContainersOnStartup;
    }

    /**
     * Returns whether EBS volumes are backed by real storage and attached as block devices inside target
     * containers.
     *
     * <p>When disabled or unavailable, attachment remains metadata-only.
     *
     * @return whether EBS volumes are backed by real storage and attached as block devices inside target containers
     */
    public boolean isVolumeBlockDevices() {
        return volumeBlockDevices;
    }

    /**
     * Returns the image used for the helper container that manages volume loop devices and storage.
     *
     * @return the image used for the helper container that manages volume loop devices and storage
     */
    public String getVolumeHelperImage() {
        return volumeHelperImage;
    }

    /**
     * Returns whether instance containers are bounded to the CPU and memory limits of their instance type.
     *
     * <p>When disabled, containers are launched without CPU or memory limits.
     *
     * @return whether instance containers are bounded to the CPU and memory limits of their instance type
     */
    public boolean isInstanceResourceLimits() {
        return instanceResourceLimits;
    }

    /**
     * Returns whether each VPC is backed by a real Docker network.
     *
     * <p>Instances then get private addresses drawn from the CIDR the caller declared, and instances in
     * different VPCs cannot route to each other.
     *
     * @return whether each VPC is backed by a real Docker network
     */
    public boolean isVpcNetworksEnabled() {
        return vpcNetworksEnabled;
    }

    /**
     * Returns the private range that substituted VPC network CIDRs are allocated from.
     *
     * <p>Used when a declared VPC CIDR is absent, malformed, outside RFC 1918, or already claimed on the
     * Docker daemon. Must itself be RFC 1918.
     *
     * @return the private range that substituted VPC network CIDRs are allocated from
     */
    public String getVpcNetworksFallbackPool() {
        return vpcNetworksFallbackPool;
    }

    /**
     * Returns the prefix length of each block handed out of the VPC network fallback pool.
     *
     * @return the prefix length of each block handed out of the VPC network fallback pool
     */
    public int getVpcNetworksFallbackPrefixLength() {
        return vpcNetworksFallbackPrefixLength;
    }

    /**
     * Returns whether VPC networks left behind by a previous run of the same Floci instance are removed at
     * startup.
     *
     * @return whether VPC networks left behind by a previous run of the same Floci instance are removed at startup
     */
    public boolean isVpcNetworksReconcileOnStartup() {
        return vpcNetworksReconcileOnStartup;
    }

    /**
     * Returns the Docker network driver used for VPC networks.
     *
     * @return the Docker network driver used for VPC networks
     */
    public String getVpcNetworksDriver() {
        return vpcNetworksDriver;
    }

    /**
     * Returns the path, inside the container, of an external EC2 image catalog file that replaces
     * the image catalog bundled with Floci (e.g. to expose locally built guest images).
     *
     * <p>The file uses the same YAML schema as Floci's bundled {@code ec2/image-catalog.yaml} and
     * must include every image that should be exposed. The path is either the one passed to
     * {@link Builder#imageCatalogFile(String)} verbatim, or a generated path pointing at the file whose
     * content was passed to {@link Builder#imageCatalog(String)}.
     *
     * @return the container path of the image catalog file, or {@link Optional#empty()} if the
     *         bundled catalog is used
     */
    public Optional<String> getImageCatalogFile() {
        return Optional.ofNullable(imageCatalogFile);
    }

    /**
     * Returns the raw image catalog content supplied via {@link Builder#imageCatalog(String)}, if any.
     * When present, this content is copied into the container at {@link #getImageCatalogFile()}.
     *
     * @return the image catalog content, or {@link Optional#empty()} if the image catalog was not
     *         configured by content
     */
    public Optional<String> getImageCatalog() {
        return Optional.ofNullable(imageCatalog);
    }

    /**
     * Returns the path, inside the container, of a JSON file of additional regional AWS-managed prefix lists.
     *
     * @return the path, inside the container, of a JSON file of additional regional AWS-managed prefix lists, or empty to keep Floci's default
     */
    public Optional<String> getManagedPrefixListCatalogPath() {
        return Optional.ofNullable(managedPrefixListCatalogPath);
    }

    /**
     * Returns whether RunInstances and StartInstances are checked against the current running vCPU usage.
     *
     * <p>The applied On-Demand Standard quota is set with {@link ServiceQuotasConfig.Builder#standardOnDemandVcpus(int)}.
     *
     * @return whether RunInstances and StartInstances are checked against the current running vCPU usage
     */
    public boolean isEnforceVcpuQuotas() {
        return enforceVcpuQuotas;
    }

    /**
     * Returns whether, when Floci itself runs as a container, an instance's address on Floci's own Docker network is reported as its public IP.
     *
     * <p>Clients on that network then reach the guest's real ports there, as they would an AWS public IP.
     *
     * @return whether, when Floci itself runs as a container, an instance's address on Floci's own Docker network is reported as its public IP
     */
    public boolean isPublicIpFromServiceNetwork() {
        return publicIpFromServiceNetwork;
    }

    /**
     * Returns the automatic public IPv4 address a mock instance gets when its launch asks for one.
     *
     * <p>Elastic IPs are allocated from 203.0.113.0/24 whatever this says, and instances with containers always report the container's reachable address.
     *
     * @return the automatic public IPv4 address a mock instance gets when its launch asks for one
     */
    public MockPublicAddresses getMockPublicAddresses() {
        return mockPublicAddresses;
    }

    /**
     * Returns the public IPv4 address of every mock instance due an automatic one while {@link MockPublicAddresses#FIXED} is selected.
     *
     * <p>Use an address the caller can reach, such as this host's own. Floci uses 127.0.0.1 while unset.
     *
     * @return the public IPv4 address of every mock instance due an automatic one while {@link MockPublicAddresses#FIXED} is selected, or empty to keep Floci's default
     */
    public Optional<String> getMockPublicAddress() {
        return Optional.ofNullable(mockPublicAddress);
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
            container.withEnv("FLOCI_SERVICES_EC2_AWS_FAITHFUL_PRIVATE_IP", String.valueOf(awsFaithfulPrivateIp));

            if (containerIpsRoutable != null) {
                container.withEnv("FLOCI_SERVICES_EC2_CONTAINER_IPS_ROUTABLE", String.valueOf(containerIpsRoutable));
            }

            container.withEnv("FLOCI_SERVICES_EC2_RECONCILE_CONTAINERS_ON_STARTUP", String.valueOf(reconcileContainersOnStartup));
            container.withEnv("FLOCI_SERVICES_EC2_VOLUME_BLOCK_DEVICES", String.valueOf(volumeBlockDevices));
            container.withEnv("FLOCI_SERVICES_EC2_VOLUME_HELPER_IMAGE", volumeHelperImage);
            container.withEnv("FLOCI_SERVICES_EC2_INSTANCE_RESOURCE_LIMITS", String.valueOf(instanceResourceLimits));
            container.withEnv("FLOCI_SERVICES_EC2_VPC_NETWORKS_ENABLED", String.valueOf(vpcNetworksEnabled));
            container.withEnv("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_POOL", vpcNetworksFallbackPool);
            container.withEnv("FLOCI_SERVICES_EC2_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH", String.valueOf(vpcNetworksFallbackPrefixLength));
            container.withEnv("FLOCI_SERVICES_EC2_VPC_NETWORKS_RECONCILE_ON_STARTUP", String.valueOf(vpcNetworksReconcileOnStartup));
            container.withEnv("FLOCI_SERVICES_EC2_VPC_NETWORKS_DRIVER", vpcNetworksDriver);

            if (imageCatalogFile != null) {
                container.withEnv("FLOCI_SERVICES_EC2_IMAGE_CATALOG_PATH", imageCatalogFile);
            }

            if (managedPrefixListCatalogPath != null) {
                container.withEnv("FLOCI_SERVICES_EC2_MANAGED_PREFIX_LIST_CATALOG_PATH", managedPrefixListCatalogPath);
            }

            container.withEnv("FLOCI_SERVICES_EC2_ENFORCE_VCPU_QUOTAS", String.valueOf(enforceVcpuQuotas));
            container.withEnv("FLOCI_SERVICES_EC2_PUBLIC_IP_FROM_SERVICE_NETWORK", String.valueOf(publicIpFromServiceNetwork));
            container.withEnv("FLOCI_SERVICES_EC2_MOCK_PUBLIC_ADDRESSES", mockPublicAddresses.name().toLowerCase(Locale.ROOT));

            if (mockPublicAddress != null) {
                container.withEnv("FLOCI_SERVICES_EC2_MOCK_PUBLIC_ADDRESS", mockPublicAddress);
            }
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

    @Override
    public void applyFileMountsToContainer(Container<?> container) {
        if (isEnabled() && imageCatalogFile != null && imageCatalog != null) {
            container.withCopyToContainer(Transferable.of(imageCatalog), imageCatalogFile);
        }
    }

    @Override
    public boolean requiresDockerSocket() {
        return isEnabled() && !mock;
    }

    /**
     * Automatic public IPv4 addresses of mock instances, see {@link Builder#mockPublicAddresses(MockPublicAddresses)}.
     */
    public enum MockPublicAddresses {
        /**
         * No automatic public address. Mock instances have no container to reach, so an address from a real AWS range
         * would send a client that connects to it to someone else's host.
         */
        NONE,
        /**
         * An address from 203.0.113.0/24, a range reserved for documentation that routes nowhere, distinct from other
         * live instances while one is free.
         */
        DOCUMENTATION,
        /**
         * The address configured with {@link Builder#mockPublicAddress(String)}, which an Elastic IP on the instance's
         * primary private address then shows too.
         */
        FIXED
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
        private boolean awsFaithfulPrivateIp = DEFAULT_AWS_FAITHFUL_PRIVATE_IP;
        private Boolean containerIpsRoutable;
        private AutoScaling autoScaling = new DefaultAutoScaling(true);
        private String imageCatalogFile;
        private String imageCatalog;
        private boolean reconcileContainersOnStartup = DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP;
        private boolean volumeBlockDevices = DEFAULT_VOLUME_BLOCK_DEVICES;
        private String volumeHelperImage = DEFAULT_VOLUME_HELPER_IMAGE;
        private boolean instanceResourceLimits = DEFAULT_INSTANCE_RESOURCE_LIMITS;
        private boolean vpcNetworksEnabled = DEFAULT_VPC_NETWORKS_ENABLED;
        private String vpcNetworksFallbackPool = DEFAULT_VPC_NETWORKS_FALLBACK_POOL;
        private int vpcNetworksFallbackPrefixLength = DEFAULT_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH;
        private boolean vpcNetworksReconcileOnStartup = DEFAULT_VPC_NETWORKS_RECONCILE_ON_STARTUP;
        private String vpcNetworksDriver = DEFAULT_VPC_NETWORKS_DRIVER;
        private String managedPrefixListCatalogPath;
        private boolean enforceVcpuQuotas = DEFAULT_ENFORCE_VCPU_QUOTAS;
        private boolean publicIpFromServiceNetwork = DEFAULT_PUBLIC_IP_FROM_SERVICE_NETWORK;
        private MockPublicAddresses mockPublicAddresses = DEFAULT_MOCK_PUBLIC_ADDRESSES;
        private String mockPublicAddress;

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
            this.awsFaithfulPrivateIp = instance.isAwsFaithfulPrivateIp();
            this.containerIpsRoutable = instance.getContainerIpsRoutable().orElse(null);
            this.autoScaling = instance.getAutoScaling();
            this.imageCatalogFile = instance.imageCatalogFile;
            this.imageCatalog = instance.imageCatalog;
            this.reconcileContainersOnStartup = instance.isReconcileContainersOnStartup();
            this.volumeBlockDevices = instance.isVolumeBlockDevices();
            this.volumeHelperImage = instance.getVolumeHelperImage();
            this.instanceResourceLimits = instance.isInstanceResourceLimits();
            this.vpcNetworksEnabled = instance.isVpcNetworksEnabled();
            this.vpcNetworksFallbackPool = instance.getVpcNetworksFallbackPool();
            this.vpcNetworksFallbackPrefixLength = instance.getVpcNetworksFallbackPrefixLength();
            this.vpcNetworksReconcileOnStartup = instance.isVpcNetworksReconcileOnStartup();
            this.vpcNetworksDriver = instance.getVpcNetworksDriver();
            this.managedPrefixListCatalogPath = instance.getManagedPrefixListCatalogPath().orElse(null);
            this.enforceVcpuQuotas = instance.isEnforceVcpuQuotas();
            this.publicIpFromServiceNetwork = instance.isPublicIpFromServiceNetwork();
            this.mockPublicAddresses = instance.getMockPublicAddresses();
            this.mockPublicAddress = instance.getMockPublicAddress().orElse(null);
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
         * Sets whether DescribeInstances and IMDS report each instance's CFN- and
         * subnet-allocated private IP (AWS-faithful) instead of the Docker container's bridge IP
         * (#1983). Routing/IMDS always use the container bridge IP regardless of this flag; only
         * the reported {@code PrivateIpAddress} changes.
         *
         * @param awsFaithfulPrivateIp {@code true} to report the AWS-faithful private IP
         *                              (default {@value DEFAULT_AWS_FAITHFUL_PRIVATE_IP})
         * @return this builder
         */
        public Builder awsFaithfulPrivateIp(boolean awsFaithfulPrivateIp) {
            this.awsFaithfulPrivateIp = awsFaithfulPrivateIp;
            return this;
        }

        /**
         * Sets whether an EC2 instance's Docker container IP is routable from the machines that
         * consume Floci's API responses (Terraform, Terratest, your shell). When {@code true},
         * DescribeInstances / DescribeAddresses report the container IP, so the address they hand
         * out accepts connections on the service's real port (22 for SSH, and every other port the
         * guest listens on) with no port mapping involved. When {@code false}, Floci keeps
         * reporting {@code 127.0.0.1} and reachability depends on the published high host ports.
         *
         * <p>Passing {@code null} (the default) means auto-detect: Floci opens a throwaway TCP
         * connection towards the container network and treats a refusal as proof of a route. Set it
         * explicitly when the probe cannot speak for your clients — most notably when Floci itself
         * runs as a container, where the probe measures container-to-container reachability rather
         * than host-to-container.
         *
         * @param containerIpsRoutable whether container IPs are routable, or {@code null} to
         *                             auto-detect (the default)
         * @return this builder
         */
        public Builder containerIpsRoutable(Boolean containerIpsRoutable) {
            this.containerIpsRoutable = containerIpsRoutable;
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
         * Sets whether Floci removes, on startup, EC2 instance containers left on the Docker daemon by a
         * previous run of the same Floci whose instance record did not survive the restart.
         *
         * <p>Stopped instances are never swept, their containers are exactly what StartInstances revives.
         *
         * @param reconcileContainersOnStartup whether Floci removes, on startup, EC2 instance containers left on the Docker daemon by a previous run of the same Floci whose instance record did not survive the restart (default {@value DEFAULT_RECONCILE_CONTAINERS_ON_STARTUP})
         * @return this builder
         */
        public Builder reconcileContainersOnStartup(boolean reconcileContainersOnStartup) {
            this.reconcileContainersOnStartup = reconcileContainersOnStartup;
            return this;
        }

        /**
         * Sets whether EBS volumes are backed by real storage and attached as block devices inside target
         * containers.
         *
         * <p>When disabled or unavailable, attachment remains metadata-only.
         *
         * @param volumeBlockDevices whether EBS volumes are backed by real storage and attached as block devices inside target containers (default {@value DEFAULT_VOLUME_BLOCK_DEVICES})
         * @return this builder
         */
        public Builder volumeBlockDevices(boolean volumeBlockDevices) {
            this.volumeBlockDevices = volumeBlockDevices;
            return this;
        }

        /**
         * Sets the image used for the helper container that manages volume loop devices and storage.
         *
         * @param volumeHelperImage the image used for the helper container that manages volume loop devices and storage (default {@value DEFAULT_VOLUME_HELPER_IMAGE})
         * @return this builder
         */
        public Builder volumeHelperImage(String volumeHelperImage) {
            this.volumeHelperImage = volumeHelperImage;
            return this;
        }

        /**
         * Sets whether instance containers are bounded to the CPU and memory limits of their instance type.
         *
         * <p>When disabled, containers are launched without CPU or memory limits.
         *
         * @param instanceResourceLimits whether instance containers are bounded to the CPU and memory limits of their instance type (default {@value DEFAULT_INSTANCE_RESOURCE_LIMITS})
         * @return this builder
         */
        public Builder instanceResourceLimits(boolean instanceResourceLimits) {
            this.instanceResourceLimits = instanceResourceLimits;
            return this;
        }

        /**
         * Sets whether each VPC is backed by a real Docker network.
         *
         * <p>Instances then get private addresses drawn from the CIDR the caller declared, and instances in
         * different VPCs cannot route to each other.
         *
         * @param vpcNetworksEnabled whether each VPC is backed by a real Docker network (default {@value DEFAULT_VPC_NETWORKS_ENABLED})
         * @return this builder
         */
        public Builder vpcNetworksEnabled(boolean vpcNetworksEnabled) {
            this.vpcNetworksEnabled = vpcNetworksEnabled;
            return this;
        }

        /**
         * Sets the private range that substituted VPC network CIDRs are allocated from.
         *
         * <p>Used when a declared VPC CIDR is absent, malformed, outside RFC 1918, or already claimed on the
         * Docker daemon. Must itself be RFC 1918.
         *
         * @param vpcNetworksFallbackPool the private range that substituted VPC network CIDRs are allocated from (default {@value DEFAULT_VPC_NETWORKS_FALLBACK_POOL})
         * @return this builder
         */
        public Builder vpcNetworksFallbackPool(String vpcNetworksFallbackPool) {
            this.vpcNetworksFallbackPool = vpcNetworksFallbackPool;
            return this;
        }

        /**
         * Sets the prefix length of each block handed out of the VPC network fallback pool.
         *
         * @param vpcNetworksFallbackPrefixLength the prefix length of each block handed out of the VPC network fallback pool (default {@value DEFAULT_VPC_NETWORKS_FALLBACK_PREFIX_LENGTH})
         * @return this builder
         */
        public Builder vpcNetworksFallbackPrefixLength(int vpcNetworksFallbackPrefixLength) {
            this.vpcNetworksFallbackPrefixLength = vpcNetworksFallbackPrefixLength;
            return this;
        }

        /**
         * Sets whether VPC networks left behind by a previous run of the same Floci instance are removed at
         * startup.
         *
         * @param vpcNetworksReconcileOnStartup whether VPC networks left behind by a previous run of the same Floci instance are removed at startup (default {@value DEFAULT_VPC_NETWORKS_RECONCILE_ON_STARTUP})
         * @return this builder
         */
        public Builder vpcNetworksReconcileOnStartup(boolean vpcNetworksReconcileOnStartup) {
            this.vpcNetworksReconcileOnStartup = vpcNetworksReconcileOnStartup;
            return this;
        }

        /**
         * Sets the Docker network driver used for VPC networks.
         *
         * @param vpcNetworksDriver the Docker network driver used for VPC networks (default {@value DEFAULT_VPC_NETWORKS_DRIVER})
         * @return this builder
         */
        public Builder vpcNetworksDriver(String vpcNetworksDriver) {
            this.vpcNetworksDriver = vpcNetworksDriver;
            return this;
        }

        /**
         * Sets the path, inside the container, of an external EC2 image catalog file that already
         * exists in the container (for example one added through a volume or another
         * {@code withCopy*} call). It replaces the image catalog bundled with Floci, uses the same
         * YAML schema, and must include every image that should be exposed.
         *
         * <p>Use {@link #imageCatalog(String)} instead to hand over just the file content and let
         * {@link Ec2Config} take care of placing the file into the container.
         *
         * <p>Calling this method clears any content previously set via {@link #imageCatalog(String)}.
         *
         * @param imageCatalogFile the container path of the image catalog file, or {@code null} to use
         *                         the bundled catalog
         * @return this builder
         */
        public Builder imageCatalogFile(String imageCatalogFile) {
            this.imageCatalogFile = imageCatalogFile;
            this.imageCatalog = null;
            return this;
        }

        /**
         * Sets the content of an external EC2 image catalog (YAML, same schema as Floci's bundled
         * {@code ec2/image-catalog.yaml}) that replaces the bundled catalog, e.g. to expose locally
         * built guest images.
         *
         * <p>The content is copied into the container under a generated, randomized path, which is
         * then used as {@link Ec2Config#getImageCatalogFile()}. Callers therefore do not need to
         * manage any files themselves.
         *
         * <p>Calling this method clears any path previously set via {@link #imageCatalogFile(String)}.
         *
         * @param imageCatalog the image catalog content, or {@code null} to use the bundled catalog
         * @return this builder
         */
        public Builder imageCatalog(String imageCatalog) {
            this.imageCatalog = imageCatalog;
            this.imageCatalogFile = null;
            return this;
        }

        /**
         * Sets the path, inside the container, of a JSON file of additional regional AWS-managed prefix lists.
         *
         * @param managedPrefixListCatalogPath the path, inside the container, of a JSON file of additional regional AWS-managed prefix lists, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder managedPrefixListCatalogPath(String managedPrefixListCatalogPath) {
            this.managedPrefixListCatalogPath = managedPrefixListCatalogPath;
            return this;
        }

        /**
         * Sets whether RunInstances and StartInstances are checked against the current running vCPU usage.
         *
         * <p>The applied On-Demand Standard quota is set with {@link ServiceQuotasConfig.Builder#standardOnDemandVcpus(int)}.
         *
         * @param enforceVcpuQuotas whether RunInstances and StartInstances are checked against the current running vCPU usage (default {@value DEFAULT_ENFORCE_VCPU_QUOTAS})
         * @return this builder
         */
        public Builder enforceVcpuQuotas(boolean enforceVcpuQuotas) {
            this.enforceVcpuQuotas = enforceVcpuQuotas;
            return this;
        }

        /**
         * Sets whether, when Floci itself runs as a container, an instance's address on Floci's own Docker network is reported as its public IP.
         *
         * <p>Clients on that network then reach the guest's real ports there, as they would an AWS public IP.
         *
         * @param publicIpFromServiceNetwork whether, when Floci itself runs as a container, an instance's address on Floci's own Docker network is reported as its public IP (default {@value DEFAULT_PUBLIC_IP_FROM_SERVICE_NETWORK})
         * @return this builder
         */
        public Builder publicIpFromServiceNetwork(boolean publicIpFromServiceNetwork) {
            this.publicIpFromServiceNetwork = publicIpFromServiceNetwork;
            return this;
        }

        /**
         * Sets the automatic public IPv4 address a mock instance gets when its launch asks for one.
         *
         * <p>Elastic IPs are allocated from 203.0.113.0/24 whatever this says, and instances with containers always report the container's reachable address.
         *
         * @param mockPublicAddresses the automatic public IPv4 address a mock instance gets when its launch asks for one (default {@link MockPublicAddresses#NONE})
         * @return this builder
         */
        public Builder mockPublicAddresses(MockPublicAddresses mockPublicAddresses) {
            this.mockPublicAddresses = mockPublicAddresses;
            return this;
        }

        /**
         * Sets the public IPv4 address of every mock instance due an automatic one while {@link MockPublicAddresses#FIXED} is selected.
         *
         * <p>Use an address the caller can reach, such as this host's own. Floci uses 127.0.0.1 while unset.
         *
         * @param mockPublicAddress the public IPv4 address of every mock instance due an automatic one while {@link MockPublicAddresses#FIXED} is selected, or {@code null} to keep Floci's default
         * @return this builder
         */
        public Builder mockPublicAddress(String mockPublicAddress) {
            this.mockPublicAddress = mockPublicAddress;
            return this;
        }

        /**
         * Creates an immutable {@link Ec2Config} from this builder.
         *
         * @return the EC2 configuration
         */
        @Override
        public Ec2Config build() {
            if (imageCatalog != null && imageCatalogFile == null) {
                this.imageCatalogFile = IMAGE_CATALOG_FILE_PREFIX + UUID.randomUUID() + IMAGE_CATALOG_FILE_SUFFIX;
            }
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

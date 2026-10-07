<p align="center">
  <img src="https://raw.githubusercontent.com/floci-io/.github/main/floci.svg#gh-light-mode-only" alt="Floci" width="500" />
  <img src="https://github.com/user-attachments/assets/edfff8b3-926c-471e-9549-77fb90a21b49#gh-dark-mode-only" alt="Floci" width="500" />
</p>

<p align="center">
  <strong>Any Cloud. Locally.</strong><br />
  Light, fluffy, and always free: Testcontainers for Java<br />
  No account. No auth token. No feature gates.
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.floci/testcontainers-floci"><img src="https://img.shields.io/maven-central/v/io.floci/testcontainers-floci?label=maven%20central&color=blue" alt="Maven Central"></a>
  <a href="https://github.com/floci-io/testcontainers-floci/actions/workflows/ci.yml"><img src="https://github.com/floci-io/testcontainers-floci/actions/workflows/ci.yml/badge.svg?branch=main" alt="CI"></a>
  <a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/license-MIT-green" alt="License: MIT"></a>
  <a href="https://github.com/floci-io/testcontainers-floci/stargazers"><img src="https://img.shields.io/github/stars/floci-io/testcontainers-floci?style=flat" alt="GitHub Stars"></a>
</p>

<p align="center">
  <a href="#quick-start">Quick Start</a> ·
  <a href="#service-configuration">Configuration</a> ·
  <a href="#the-floci-emulators">Emulators</a> ·
  <a href="https://floci.io/floci/testcontainers/java/">Docs</a>
</p>

---

## What is this?

[Testcontainers](https://testcontainers.com/) modules for [Floci](https://github.com/floci-io), the free, open-source
local cloud emulators. Each module starts a Floci emulator container for your integration tests and gives you an
endpoint and credentials to point the cloud SDK at, plus a typed, per-service configuration API over the emulator's
environment variables. No cloud account, no auth token.

| Module                                                                                                         | Description                                                  |
|----------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| [`testcontainers-floci`](#aws-testcontainers-floci)                                                            | Testcontainers module for starting a Floci (AWS) container   |
| [`testcontainers-floci-az`](#azure-testcontainers-floci-az)                                                    | Testcontainers module for starting a Floci Azure container   |
| [`testcontainers-floci-gcp`](#gcp-testcontainers-floci-gcp)                                                    | Testcontainers module for starting a Floci GCP container     |
| `testcontainers-floci-core`                                                                                    | Shared base classes of the modules above (not used directly) |
| [`spring-boot-testcontainers-floci`](#module-spring-boot-testcontainers-floci-decommissioned) (decommissioned) | Superseded by Spring Cloud AWS's own testcontainers module   |

### The Floci emulators

testcontainers-floci is the Java member of the [Floci](https://github.com/floci-io) Testcontainers family. Floci is
named after [floccus](https://en.wikipedia.org/wiki/Cirrocumulus_floccus), the cloud formation that looks like popcorn.

| Emulator                                           | Cloud | Port |                           Supported                            |
|----------------------------------------------------|-------|:----:|:--------------------------------------------------------------:|
| [floci](https://github.com/floci-io/floci)         | AWS   | 4566 |     ✅ [`testcontainers-floci`](#aws-testcontainers-floci)     |
| [floci-az](https://github.com/floci-io/floci-az)   | Azure | 4577 | ✅ [`testcontainers-floci-az`](#azure-testcontainers-floci-az) |
| [floci-gcp](https://github.com/floci-io/floci-gcp) | GCP   | 4588 | ✅ [`testcontainers-floci-gcp`](#gcp-testcontainers-floci-gcp) |
| [floci-oci](https://github.com/floci-io/floci-oci) | OCI   | 4599 |                            Planned                             |

## Installation

### Version compatibility

| testcontainers-floci | Spring Boot integration                                                                                   | Testcontainers | Release badges                                                                                                                                                                       |
|----------------------|-----------------------------------------------------------------------------------------------------------|----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **2.x**              | via [`spring-cloud-aws-testcontainers`](#module-spring-boot-testcontainers-floci-decommissioned) (4.1.0+) | 2.x            | [![Maven Central](https://img.shields.io/maven-central/v/io.floci/testcontainers-floci)](https://central.sonatype.com/artifact/io.floci/testcontainers-floci)                        |
| **1.x**              | `spring-boot-testcontainers-floci` (Spring Boot 3.5.x / Spring Cloud AWS 3.4.x)                           | 1.x            | [![Maven Central](https://img.shields.io/maven-central/v/io.floci/testcontainers-floci?filter=1.*)](https://img.shields.io/maven-central/v/io.floci/testcontainers-floci?filter=1.*) |

### AWS: testcontainers-floci

**Maven:**

```xml
<dependency>
    <groupId>io.floci</groupId>
    <artifactId>testcontainers-floci</artifactId>
    <version>${testcontainers-floci.version}</version>
    <scope>test</scope>
</dependency>
```

**Gradle (Kotlin DSL):**

```kotlin
testImplementation("io.floci:testcontainers-floci:${testcontainersFlociVersion}")
```

**Gradle (Groovy DSL):**

```groovy
testImplementation "io.floci:testcontainers-floci:${testcontainersFlociVersion}"
```

### Azure: testcontainers-floci-az

```xml
<dependency>
    <groupId>io.floci</groupId>
    <artifactId>testcontainers-floci-az</artifactId>
    <version>${testcontainers-floci.version}</version>
    <scope>test</scope>
</dependency>
```

### GCP: testcontainers-floci-gcp

```xml
<dependency>
    <groupId>io.floci</groupId>
    <artifactId>testcontainers-floci-gcp</artifactId>
    <version>${testcontainers-floci.version}</version>
    <scope>test</scope>
</dependency>
```

### Spring Boot integration

> **`spring-boot-testcontainers-floci` has been decommissioned on `main` and is no longer published for
> `testcontainers-floci` 2.x.** The same `@ServiceConnection` integration between `FlociContainer` and Spring Cloud AWS
> is now provided directly by the [Spring Cloud AWS](https://awspring.io/) project itself, via its own
> `spring-cloud-aws-testcontainers` module, starting from **Spring Cloud AWS 4.1.0**. Depend on that module instead:
>
> ```xml
> <dependency>
>     <groupId>io.awspring.cloud</groupId>
>     <artifactId>spring-cloud-aws-testcontainers</artifactId>
>     <version>4.1.0</version>
>     <scope>test</scope>
> </dependency>
> ```
>
> It is still used together with `testcontainers-floci` (for `FlociContainer` itself) — only the Spring Boot glue
> code moves to Spring Cloud AWS. See
> the [Spring Cloud AWS documentation](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html)
> for usage details.
>
> The `1.x` line (Spring Boot 3.x / Spring Cloud AWS 3.4.x) still ships `spring-boot-testcontainers-floci` on the
> `releases/1.x` branch and is unaffected by this change.

## Quick start

### AWS (Java)

```java
import io.floci.testcontainers.FlociContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class S3IntegrationTest {

    @Container
    static FlociContainer floci = new FlociContainer();

    @Test
    void shouldCreateBucket() {
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create(floci.getEndpoint()))
                .region(Region.of(floci.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(floci.getAccessKey(), floci.getSecretKey())))
                .forcePathStyle(true)
                .build();

        s3.createBucket(b -> b.bucket("my-bucket"));

        var buckets = s3.listBuckets().buckets();
        assertThat(buckets).anyMatch(b -> b.name().equals("my-bucket"));
    }
}
```

### AWS (Kotlin)

```kotlin
import io.floci.testcontainers.FlociContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Testcontainers
class S3IntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val floci = FlociContainer()
    }

    @Test
    fun `should create bucket`() {
        val s3 = S3Client.builder()
            .endpointOverride(URI.create(floci.getEndpoint()))
            .region(Region.of(floci.getRegion()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(floci.accessKey, floci.secretKey)
                )
            )
            .forcePathStyle(true)
            .build()

        s3.createBucket { it.bucket("my-bucket") }

        val buckets = s3.listBuckets().buckets()
        assertThat(buckets).anyMatch { it.name() == "my-bucket" }
    }
}
```

### Azure

```java
@Testcontainers
class BlobStorageTest {

    @Container
    static FlociAzContainer floci = new FlociAzContainer();

    @Test
    void shouldUploadBlob() {
        BlobServiceClient blobs = new BlobServiceClientBuilder()
                .connectionString(floci.getStorageConnectionString())
                .buildClient();

        BlobClient blob = blobs.createBlobContainer("my-container").getBlobClient("hello.txt");
        blob.upload(BinaryData.fromString("hello"));

        assertThat(blob.downloadContent().toString()).isEqualTo("hello");
    }
}
```

Storage data planes live under account-prefixed paths of the default account `devstoreaccount1`
(`getBlobEndpoint()`, `getQueueEndpoint()`, `getTableEndpoint()`); ARM management calls go to
`getEndpoint() + "/subscriptions/" + getSubscriptionId() + ...`.

### GCP

```java
@Testcontainers
class StorageTest {

    @Container
    static FlociGcpContainer floci = new FlociGcpContainer();

    @Test
    void shouldUploadObject() {
        Storage storage = StorageOptions.newBuilder()
                .setHost(floci.getEndpoint())
                .setProjectId(floci.getProjectId())
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();

        String bucket = storage.create(BucketInfo.of("my-bucket")).getName();
        storage.create(BlobInfo.newBuilder(bucket, "hello.txt").build(), "hello".getBytes(UTF_8));

        assertThat(storage.readAllBytes(bucket, "hello.txt")).asString(UTF_8).isEqualTo("hello");
    }
}
```

REST-based clients such as Cloud Storage or BigQuery take `getEndpoint()` as host.

## Service configuration

### AWS

| Method                                | Description                                                                                                    |
|---------------------------------------|----------------------------------------------------------------------------------------------------------------|
| `FlociContainer()`                    | Creates a container with the default image (`floci/floci:latest`)                                              |
| `FlociContainer(String)`              | Creates a container with a custom image tag                                                                    |
| `withRegion(String)`                  | Sets the AWS region (default: `us-east-1`)                                                                     |
| `withDefaultAvailabilityZone(String)` | Sets the default availability zone (default: `us-east-1a`)                                                     |
| `withDefaultAccountId(String)`        | Sets the default AWS account ID (default: `000000000000`)                                                      |
| `withLogLevel(Level)`                 | Sets the Floci log level (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`)                                           |
| `withAiMockConfigFile(String)`        | Points the fixed-stub AI services (Textract, Comprehend, Rekognition, Translate) at a container mock file      |
| `withAiMockConfig(String)`            | Same, but takes the mock-response file content and copies it into the container for you                        |
| `withDedicatedNetwork()`              | Creates a dedicated Docker network shared by Floci and its sibling containers (RDS, Lambda, ElastiCache, etc.) |
| `withDockerSocket(boolean)`           | Overrides whether the host Docker socket is mounted, bypassing auto-detection (see below)                      |
| `withTlsConfig(...)`                  | Configures TLS/HTTPS (self-signed by default; optionally provide cert/key paths)                               |
| `withStorageConfig(...)`              | Configures persistent storage and volume behaviour                                                             |
| `withSecurityConfig(...)`             | Configures security settings (CORS, private JWT issuer targets, network exposure)                              |
| `withProtocolsConfig(...)`            | Configures RPC wire-protocol handling (e.g. strict protocol claiming)                                          |
| `withAuthConfig(...)`                 | Configures authentication settings (e.g. SigV4 signature validation, presign secret)                           |
| `withInitHooksConfig(...)`            | Configures lifecycle init hook execution (shell, timeouts)                                                     |
| `withPartitionsConfig(...)`           | Configures the served AWS partition and partition/region strictness                                            |
| `withNetworkConfig(...)`              | Configures network settings (e.g. security-group enforcement for EC2/ECS containers)                           |
| `with*Config(...)`                    | Configures service-specific settings                                                                           |

Each AWS service emulated by Floci can be individually configured via a `with*Config(...)` method on
`FlociContainer`. Every service configuration supports at least an `enabled(boolean)` flag to enable or
disable the service. Some services expose additional settings. See the
[Floci documentation](https://floci.io/floci/services/) for the full list of supported services.

Example — disable a service and customize another:

```java
FlociContainer floci = new FlociContainer()
        .withSqsConfig(c -> c.defaultVisibilityTimeout(60).maxMessageSize(131072))
        .withDynamoDbConfig(c -> c.enabled(false));
```

#### Docker socket

Some services (RDS, Lambda, ElastiCache, ECS, and others) spin up sibling Docker containers and need
access to the host Docker socket. `FlociContainer` mounts the socket automatically, but only when at
least one currently enabled service actually needs it — e.g. a container that only uses S3/SQS/DynamoDB
never gets the socket mounted once the Docker-backed services are disabled (all services are enabled by default).

Use `withDockerSocket(boolean)` to override this auto-detection entirely, regardless of which services
are enabled:

```java
// Never mount the socket, e.g. on hosts where mounting it doesn't work
// (such as rootless Podman with SELinux) and no Docker-backed service is needed
FlociContainer floci = new FlociContainer().withDockerSocket(false);

// Always mount the socket, even if no currently enabled service is detected as needing it
FlociContainer floci = new FlociContainer().withDockerSocket(true);
```

### Azure

| Method                      | Description                                                                                     |
|-----------------------------|-------------------------------------------------------------------------------------------------|
| `FlociAzContainer()`        | Creates a container with the default image (`floci/floci-az:latest`)                            |
| `FlociAzContainer(String)`  | Creates a container with a custom image tag                                                     |
| `withLogLevel(Level)`       | Sets the Floci Azure log level (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`)                      |
| `withDedicatedNetwork()`    | Creates a dedicated Docker network shared by Floci Azure and the containers it spawns           |
| `withDockerSocket(boolean)` | Overrides whether the host Docker socket is mounted, bypassing auto-detection                   |
| `withTlsConfig(...)`        | Configures TLS/HTTPS (self-signed by default; optionally provide cert/key paths)                |
| `withAuthConfig(...)`       | Configures authentication, e.g. keys of additional storage accounts used to validate SAS tokens |
| `with*Config(...)`          | Configures service-specific settings, e.g. `withServiceBusConfig(c -> c.mocked(false))`         |

Docker-backed services (Functions, AKS, Container Registry, Redis, Event Hubs, Service Bus, SQL Database,
PostgreSQL, MySQL, MariaDB, Cosmos DB API engines, Container Instances, Virtual Machines, Container Apps) mount the
host Docker socket automatically while they are enabled and not `mocked`, exactly like the AWS module. Their sidecar
containers publish their ports directly on the Docker host; the port ranges they use default to 10 ports each (e.g.
`withAksConfig(c -> c.apiServerPortRange(6443, 10))`).

#### HTTPS

Several Azure SDKs (Key Vault, App Configuration, Communication Services, Cosmos DB) only talk HTTPS. Enable TLS and
let the client trust the certificate Floci Azure serves; HTTP and HTTPS share the same port:

```java
FlociAzContainer floci = new FlociAzContainer().withTlsConfig(c -> c.enabled(true));
floci.start();

String certificatePem = floci.getTlsCertificate();      // add it to the trust store of your HTTP client
SecretClient secrets = new SecretClientBuilder()
        .vaultUrl(floci.getHttpsEndpoint() + "/devstoreaccount1-keyvault")
        // ...
        .buildClient();
```

> **Note:** Floci Azure generates some URLs (e.g. the polling URL of long-running Email operations) from its own base
> URL `http://localhost:4577`, which does not match the randomly mapped host port of the container. Clients following
> such URLs need to rewrite them to `getEndpoint()`/`getHttpsEndpoint()`.

### GCP

| Method                      | Description                                                                         |
|-----------------------------|-------------------------------------------------------------------------------------|
| `FlociGcpContainer()`       | Creates a container with the default image (`floci/floci-gcp:latest`)               |
| `FlociGcpContainer(String)` | Creates a container with a custom image tag                                         |
| `withProjectId(String)`     | Sets the default project id                                                         |
| `withLogLevel(Level)`       | Sets the Floci GCP log level (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`)            |
| `withDedicatedNetwork()`    | Creates a dedicated Docker network shared by Floci GCP and the containers it spawns |
| `withDockerSocket(boolean)` | Overrides whether the host Docker socket is mounted, bypassing auto-detection       |
| `withTlsConfig(...)`        | Configures TLS/HTTPS (self-signed by default; optionally provide cert/key paths)    |
| `with*Config(...)`          | Configures service-specific settings, e.g. `withCloudSqlConfig(c -> c.mock(true))`  |

Docker-backed services (Managed Kafka, Cloud SQL, Cloud Run, GKE and the DuckDB query engine of BigQuery) mount the
host Docker socket automatically while they are enabled and not `mock`ed, exactly like the other modules. Their
sidecar containers publish their ports directly on the Docker host (e.g. the IP address and port of a Cloud SQL
instance are reachable from the test); the GKE API server port range defaults to 10 ports
(`withGkeConfig(c -> c.apiServerPortRange(6550, 10))`). Cloud Run services are invoked through the main port, addressed
by the `Host` header of the service URL.

gRPC-based clients (Pub/Sub, Secret Manager, KMS, Logging, ...) connect to `getEmulatorHost()` (`host:port`) via a
plaintext channel; the same value can be used for `PUBSUB_EMULATOR_HOST`-style settings:

```java
TopicAdminClient topics = TopicAdminClient.create(TopicAdminSettings.newBuilder()
        .setTransportChannelProvider(InstantiatingGrpcChannelProvider.newBuilder()
                .setEndpoint(floci.getEmulatorHost())
                .setChannelConfigurator(ManagedChannelBuilder::usePlaintext)
                .build())
        .setCredentialsProvider(NoCredentialsProvider.create())
        .build());
topics.createTopic(TopicName.of(floci.getProjectId(), "my-topic"));
```

#### HTTPS

Enable TLS for clients that insist on HTTPS and let them trust the certificate Floci GCP serves; HTTP and HTTPS share
the same port:

```java
FlociGcpContainer floci = new FlociGcpContainer().withTlsConfig(c -> c.enabled(true));
floci.start();

String certificatePem = floci.getTlsCertificate();      // add it to the trust store of your HTTP or gRPC client
String endpoint = floci.getHttpsEndpoint();
```

## Container options

### AWS

| Method                         | Description                                                       | Default        |
|--------------------------------|-------------------------------------------------------------------|----------------|
| `getEndpoint()`                | HTTP endpoint URL (e.g. `http://localhost:32781`)                 | —              |
| `getRegion()`                  | Configured AWS region                                             | `us-east-1`    |
| `getDefaultAvailabilityZone()` | Configured default availability zone                              | `us-east-1a`   |
| `getDefaultAccountId()`        | Configured default AWS account ID                                 | `000000000000` |
| `getAccessKey()`               | AWS access key                                                    | `test`         |
| `getSecretKey()`               | AWS secret key                                                    | `test`         |
| `getLogLevel()`                | Configured log level                                              | `WARN`         |
| `getDedicatedNetworkName()`    | Name of the dedicated Docker network, or `null` if not configured | `null`         |
| `getTlsConfig()`               | Current TLS configuration                                         | —              |
| `getStorageConfig()`           | Current storage configuration                                     | —              |
| `getSecurityConfig()`          | Current security configuration                                    | —              |
| `getProtocolsConfig()`         | Current protocols configuration                                   | —              |
| `getAuthConfig()`              | Current auth configuration                                        | —              |
| `getInitHooksConfig()`         | Current init hooks configuration                                  | —              |
| `getPartitionsConfig()`        | Current partitions configuration                                  | —              |
| `getNetworkConfig()`           | Current network configuration                                     | —              |
| `get*Config()`                 | Current configuration of a service                                | —              |

### Azure

| Method                         | Description                                                   | Default                                |
|--------------------------------|---------------------------------------------------------------|----------------------------------------|
| `getEndpoint()`                | HTTP endpoint URL (e.g. `http://localhost:32781`)             | —                                      |
| `getHttpsEndpoint()`           | HTTPS endpoint URL (requires TLS to be enabled)               | —                                      |
| `getTlsCertificate()`          | PEM certificate served for HTTPS (requires TLS to be enabled) | —                                      |
| `getAccountName()`             | Default storage account                                       | `devstoreaccount1`                     |
| `getAccountKey()`              | Key of the default storage account                            | well-known development storage key     |
| `getBlobEndpoint()`            | Blob Storage endpoint of the default account                  | —                                      |
| `getQueueEndpoint()`           | Queue Storage endpoint of the default account                 | —                                      |
| `getTableEndpoint()`           | Table Storage endpoint of the default account                 | —                                      |
| `getStorageConnectionString()` | Connection string for Blob, Queue and Table Storage           | —                                      |
| `getSubscriptionId()`          | Default subscription id (`withArmConfig(...)`)                | `00000000-0000-0000-0000-000000000001` |
| `getTenantId()`                | Default Microsoft Entra ID tenant id (`withEntraConfig(...)`) | `00000000-0000-0000-0000-000000000002` |
| `getLogLevel()`                | Configured log level                                          | `WARN`                                 |
| `get*Config()`                 | Current configuration of a service or of TLS/auth             | —                                      |

### GCP

| Method                | Description                                                   | Default       |
|-----------------------|---------------------------------------------------------------|---------------|
| `getEndpoint()`       | HTTP endpoint URL (e.g. `http://localhost:32781`)             | —             |
| `getEmulatorHost()`   | `host:port` for gRPC channels and `*_EMULATOR_HOST` settings  | —             |
| `getHttpsEndpoint()`  | HTTPS endpoint URL (requires TLS to be enabled)               | —             |
| `getTlsCertificate()` | PEM certificate served for HTTPS (requires TLS to be enabled) | —             |
| `getProjectId()`      | Default project id                                            | `floci-local` |
| `getLogLevel()`       | Configured log level                                          | `WARN`        |
| `get*Config()`        | Current configuration of a service or of TLS                  | —             |

## Docker image tags

By default each module runs the floating `latest` tag of its emulator image (`floci/floci:latest`,
`floci/floci-az:latest`), so you always test against the current emulator. Pass an image name to the constructor to
pin a release or follow `main`:

```java
new FlociContainer("floci/floci:x.y.z");      // a specific release
new FlociAzContainer("floci/floci-az:nightly"); // built from main every night
new FlociGcpContainer("floci/floci-gcp:latest"); // last release
```

Every emulator publishes `latest`, `x.y.z` and `nightly` tags.

## Requirements

- Java 17+
- Docker

## Building and testing

```bash
mvn -B verify                                              # all modules, unit and integration tests
mvn -pl testcontainers-floci test -Dtest=IamConfigTest     # a single test class
```

There is no separate integration-test phase: `mvn verify` starts real Floci containers, so Docker must be running.
See [CONTRIBUTING.md](CONTRIBUTING.md) for the project layout, the branching model, and how to add a service.

## Other languages

| Language             | Repository                                                                             |
|----------------------|----------------------------------------------------------------------------------------|
| Java                 | **testcontainers-floci** (this repo)                                                   |
| Node.js / TypeScript | [testcontainers-floci-node](https://github.com/floci-io/testcontainers-floci-node)     |
| Python               | [testcontainers-floci-python](https://github.com/floci-io/testcontainers-floci-python) |
| Go                   | [testcontainers-floci-go](https://github.com/floci-io/testcontainers-floci-go)         |
| .NET                 | [testcontainers-floci-dotnet](https://github.com/floci-io/testcontainers-floci-dotnet) |

## Community

- 💬 [Slack](https://join.slack.com/t/floci/shared_invite/zt-3tjn02s3q-A00kEjJ1cZxsg_imTfy6Cw): quick questions and
  community chat
- 🗣️ [GitHub Discussions](https://github.com/orgs/floci-io/discussions): ideas, design tradeoffs, and proposals
- [CONTRIBUTING.md](CONTRIBUTING.md) · [SECURITY.md](SECURITY.md) · [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) · [MAINTAINERS.md](MAINTAINERS.md)

## License

MIT. See [LICENSE](LICENSE).

---

<div align="center">

Floci™ is a trademark of Hector Ventura. Code is MIT-licensed; see
[TRADEMARK.md](https://github.com/floci-io/.github/blob/main/TRADEMARK.md) for name and logo use.

</div>

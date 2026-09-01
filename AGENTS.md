# AGENT.md

## Project

Testcontainers module for [Floci](https://github.com/floci-io/floci) — a local AWS emulator (port 4566, 19+ services).

## Structure

- `testcontainers-floci/` — Core module: `FlociContainer` extending `GenericContainer`
- `spring-boot-testcontainers-floci/` — Spring Boot 4 + Spring Cloud AWS 4 integration via `@ServiceConnection`

## Build

```
mvn verify
```

Requires Docker running — `mvn test`/`mvn verify` run both plain unit tests (config classes, no Docker) and the
Docker-backed `*ServiceTest` integration tests in the same surefire phase; there is no separate failsafe/`verify`-only
split. Run a single test class or module with:

```
mvn -pl testcontainers-floci test -Dtest=IamConfigTest
mvn -pl testcontainers-floci test -Dtest=IamServiceTest
```

## Architecture

`FlociContainer` (`testcontainers-floci/src/main/java/io/floci/testcontainers/FlociContainer.java`) is the single
entry point; everything else hangs off it:

- **Cross-cutting config** (`config/`: `TlsConfig`, `StorageConfig`, `DuckDbConfig`, `SecurityConfig`,
  `ProtocolsConfig`) and **per-service config** (`config/services/`, one class per AWS service, e.g. `IamConfig`,
  `S3Config`) are immutable value classes built via a nested `Builder`, each extending `AbstractServiceConfig`/
  `AbstractServiceConfigBuilder` for the shared `enabled` flag and `toBuilder()` round-trip.
- Each service config's `applyEnvVarsToContainer(Container<?>)` sets its own `FLOCI_SERVICES_<SERVICE>_<PROPERTY>`
  env vars (only when enabled); some also override `applyExposedPortsToContainer(...)` for services that need extra
  ports (RDS, Lambda, ElastiCache, EC2, ECR).
- `FlociContainer` holds one field + a `with<Service>Config(Consumer<Builder>)`/`get<Service>Config()` pair per
  service, and registers every service field in `serviceConfigAccessors` (a `List<ServiceConfigAccessor<?>>`, a
  generic getter/setter pair) so operations like `disableAllServices()` and the env-var/port wiring
  (`configureEnvVars()`/`configureExposedPorts()`, called from the constructor and after every `with*Config` call)
  can iterate all services generically without a big switch. Adding a new service means touching all of these — see
  "Adding support for a new Floci service" in CONTRIBUTING.md for the exact steps and file locations.
- `spring-boot-testcontainers-floci` wires `FlociContainer` into Spring via a `ContainerConnectionDetailsFactory`
  (`FlociAwsContainerConnectionDetailsFactory`) producing Spring Cloud AWS's `AwsConnectionDetails`, plus an
  `@AutoConfiguration` (`FlociAwsAutoConfiguration`) that force-enables S3 path-style access. Registered the old way
  in `META-INF/spring.factories` *and* the new way in `META-INF/spring/…AutoConfiguration.imports` — keep both in
  sync when adding auto-configurations.

## Testing

- `services/*ServiceTest` (Docker required) all extend package-private `AbstractServiceTest`
  (`testcontainers-floci/src/test/java/io/floci/testcontainers/services/AbstractServiceTest.java`), which starts one
  `FlociContainer` singleton per JVM in a static initializer and exposes a `client(builder)` helper that wires
  endpoint/region/credentials onto an AWS SDK client builder.
- `config/services/*ConfigTest` (no Docker) test each config class's builder/env-var logic in isolation.
- `FlociContainerServicesConfigTest` (no Docker) is the container-level counterpart to `*ConfigTest`: it proves that
  every config exposed by `FlociContainer` is actually *picked up* by the container. It has **exactly one
  `@Test` per config class** — one per service config in `config/services/`, plus one per cross-cutting config in
  `config/` (`DuckDbConfig`, `SecurityConfig`, `ProtocolsConfig`, `AuthConfig`, `InitHooksConfig`). Every test calls
  the shared `assertConfigWired(...)` helper, which builds a `new FlociContainer()`, applies the `with<X>Config(...)`
  mutator, and asserts three things:
    1. the changed value round-trips back out via `get<X>Config()`;
    2. the matching `FLOCI_*` env var is present on `container.getEnvMap()` with the expected string value;
    3. `container.getExposedPorts()` contains an expected port — `FlociContainer.PORT` for most services, or the
       service's own port for the ~12 services whose config overrides `applyExposedPortsToContainer(...)` (RDS,
       Lambda, ElastiCache, EC2, ECR, EKS, ELBv2, IoT, MWAA, MSK, Neptune, MemoryDB).
  When adding a new service (or config class), add one `shouldWire<X>ConfigIntoContainer()` method following the
  pattern of its neighbours: change **one** property to a non-default value (add a second only when required to make
  the env var / port apply, e.g. `enabled(true)` for a service that's off by default, or `exposeRuntimePorts(true)`
  for Lambda). Pick a property that maps to a `FLOCI_*` env var; fall back to `enabled(false)` for services whose
  only setting is the enabled flag. Find the exact env-var name in the config class's `applyEnvVarsToContainer(...)`.
- `FlociContainerTest.shouldDisableAllServices()` asserts `disableAllServices()` disables every service, but it
  enumerates each `container.get<Service>Config()` **explicitly** (not via `serviceConfigAccessors`). Whenever you
  add a new service config, add its getter to that assertion list too — otherwise the new service is silently
  unchecked. When adding a service, verify this test lists all config classes under `config/services/` and fill in
  any gaps.

## Key Tech

- Java 17, Maven multi-module
- Testcontainers 2.x, Spring Boot 4.x, Spring Cloud AWS 4.x
- Conventional commits → release-please for versioning (release PR → tag → Maven Central)
- Publishes to Maven Central (GPG signed)

## Conventions

- Use conventional commits (`feat:`, `fix:`, `chore:`, etc.)
- CONTRIBUTING.md gives some details about contribution guidelines that should be followed when contributing 
  to the project.
- Do not add a "Co-Authored-By" (or similar) line to commit messages attributing the commit to an
  agent/AI tool. Agents working in this repo should omit that trailer entirely.
# Contributing to Testcontainers Floci

Thank you for your interest in contributing! This document explains how to get started, how to run the tests, how the
branching model works, and what conventions to follow.

Please read and follow the [Code of Conduct](CODE_OF_CONDUCT.md).

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker (required for integration tests)

### Fork and clone

1. Fork the repository on GitHub.
2. Clone your fork:
   ```bash
   git clone https://github.com/<your-username>/testcontainers-floci.git
   cd testcontainers-floci
   ```
3. Add the upstream remote:
   ```bash
   git remote add upstream https://github.com/floci-io/testcontainers-floci.git
   ```

### Build

```bash
mvn verify
```

This compiles all modules, runs unit tests, and runs integration tests against a real Floci container. Docker must be
running for the integration tests to pass.

## Project Structure

```
testcontainers-floci/             Core module — FlociContainer and all config classes
  src/main/java/
    io/floci/testcontainers/
      FlociContainer.java         Main container class
      config/                     TlsConfig, StorageConfig
      config/services/            Per-service config classes (one per AWS service)
  src/test/java/
    io/floci/testcontainers/
      FlociContainerServicesConfigTest.java  Unit tests for service config wiring (no Docker)
      config/services/            Unit tests for individual config classes (no Docker)
      services/                   Integration tests per AWS service (Docker required)
        AbstractServiceTest.java  Shared singleton FlociContainer used by all service tests

spring-boot-testcontainers-floci/ Spring Boot auto-configuration module
  src/main/java/
    io/floci/testcontainers/springboot/
      FlociContainerConnectionDetailsFactory.java  @ServiceConnection wiring
```

## Branching Model

| Branch          | Purpose                                                                    |
|-----------------|----------------------------------------------------------------------------|
| `main`          | Active development; targets the latest major version line                  |
| `releases/1.x`  | Maintenance branch for the 1.x line (Spring Boot 3.x / Testcontainers 1.x) |

**Where to target your pull request:**

- Bug fixes and new features for the current major version → `main`
- Backports of critical bug fixes for the 1.x line → `releases/1.x`

When in doubt, open the PR against `main` and mention in the description if a backport to `releases/1.x` is needed.

## Making a Contribution

1. Sync with upstream before starting:
   ```bash
   git fetch upstream
   git checkout main
   git rebase upstream/main
   ```
2. Create a feature branch:
   ```bash
   git checkout -b feat/my-new-feature
   ```
3. Make your changes, add tests, and ensure `mvn verify` passes.
4. Commit following the [commit message conventions](#commit-messages) below.
5. Push and open a pull request against `main` (or `releases/1.x` for backports).

### Adding support for a new Floci service

When Floci adds a new service, the typical steps are:

1. Create `testcontainers-floci/src/main/java/io/floci/testcontainers/config/services/<Service>Config.java`
   following the pattern of existing config classes (extend `AbstractServiceConfig`, inner `Builder`, env var naming
   `FLOCI_SERVICES_<SERVICE>_<PROPERTY>`). If the service creates sibling Docker containers (like RDS or Lambda),
   override `requiresDockerSocket()` to return `true` while enabled (and, for services with a docker-less `mock`
   mode, only while not mocked) — this drives whether `FlociContainer` mounts the host Docker socket.
2. Wire the config into `FlociContainer`: add a field, a `get<Service>Config()` getter, a `with<Service>Config(...)`
   method, and register the field in the `serviceConfigAccessors` list (this drives `configureEnvVars()` /
   `configureExposedPorts()` / `configureFileMounts()` / `disableAllServices()`).
3. Add a config unit test in `testcontainers-floci/src/test/java/io/floci/testcontainers/config/services/`.
4. Add a test method to `FlociContainerServicesConfigTest` for the container wiring, and add the new
   `container.get<Service>Config()` to the assertion list in `FlociContainerTest.shouldDisableAllServices()`
   (that test enumerates every service config explicitly, so a new service must be added there or it goes
   unchecked).
5. Add an integration test in `testcontainers-floci/src/test/java/io/floci/testcontainers/services/` extending
   `AbstractServiceTest`.

## Commit Messages

This project uses [Conventional Commits](https://www.conventionalcommits.org/). Commit messages directly determine
the release version that is published automatically.

### Format

```
<type>[optional scope]: <short description>

[optional body]

[optional footer(s)]
```

### Types and version impact

| Prefix                         | Version bump          | Example                                     |
|--------------------------------|-----------------------|---------------------------------------------|
| `fix:`                         | Patch (0.1.0 → 0.1.1) | `fix: handle null region gracefully`        |
| `feat:`                        | Minor (0.1.0 → 0.2.0) | `feat: add withServices() configuration`    |
| `feat!:` or `BREAKING CHANGE:` | Major (0.1.0 → 1.0.0) | `feat!: use next Spring Boot major version` |
| `chore:`, `docs:`, `ci:`       | No release            | `docs: update README examples`              |

A commit linter runs on every pull request and will flag messages that do not follow this format.

## Releases

Releases are automated by [release-please](https://github.com/googleapis/release-please), running independently on
`main` and `releases/1.x`. On every push it:

1. Parses the conventional commit history since the last release tag to determine the next version and changelog.
2. Opens (and keeps up to date) a release pull request with the generated changelog and the corresponding POM version
   bump for all modules.
3. Once a maintainer merges that pull request, tags the release, creates the GitHub Release, and triggers a follow-up
   job that builds and publishes the artifacts to Maven Central (GPG-signed).

Contributors do not need to manage versions or tags — just follow the [commit message conventions](#commit-messages)
above and release-please takes care of the rest.

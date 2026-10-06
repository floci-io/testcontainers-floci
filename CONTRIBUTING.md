# Contributing to Testcontainers Floci

Thank you for your interest in contributing! This document explains how to get started, how to run the tests, how the
branching model works, and what conventions to follow.

Please read and follow the [Code of Conduct](CODE_OF_CONDUCT.md).

**Join us on [Slack](https://join.slack.com/t/floci/shared_invite/zt-3tjn02s3q-A00kEjJ1cZxsg_imTfy6Cw)**: it is the fastest way to reach maintainers.

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
testcontainers-floci-core/        Shared base of all modules (provider-independent)
  src/main/java/
    io/floci/testcontainers/core/
      AbstractFlociContainer.java Service config registry, env var/port/file-mount wiring, Docker socket detection
      config/services/            AbstractServiceConfig, AbstractServiceConfigBuilder
  src/test/java/                  Test helpers published as test-jar (ContainerUtils, TransferableCopyInspector)
testcontainers-floci/             AWS module — FlociContainer and all config classes
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
testcontainers-floci-az/          Azure module — FlociAzContainer (extends AbstractFlociContainer)
  src/main/java/
    io/floci/testcontainers/az/
      FlociAzContainer.java       Main container class
      config/                     TlsConfig, AuthConfig
      config/services/            Per-service config classes (one per Floci Azure service)
  src/test/java/                  Same layout as the AWS module (FlociAzContainerServicesConfigTest, config/services/,
                                  services/ with a TLS-enabled AbstractServiceTest)
```

The AWS module does not use `testcontainers-floci-core` (yet); the other provider modules build on it.

> **Note:** `spring-boot-testcontainers-floci` was removed on `main`. Use the
> [`spring-cloud-aws-testcontainers`](https://github.com/awspring/spring-cloud-aws) module (from Spring Cloud AWS
> 4.1.0 onwards) instead — see the [README](README.md#spring-boot-integration) for
> details.

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

### Adding support for a new Floci Azure service

Same steps as above, in `testcontainers-floci-az`:

1. Create `config/services/<Service>Config.java` extending the core `AbstractServiceConfig` (use the `super(builder)`
   constructor), with env vars named `FLOCI_AZ_SERVICES_<ACCESSOR>_<PROPERTY>`. Keep Floci's property names (Floci
   Azure calls the docker-less mode `mocked`), and override `requiresDockerSocket()` for Docker-backed services.
2. In `FlociAzContainer`, add a `ServiceConfigRef` field via `registerServiceConfig(...)` (in Floci's
   `ServicesConfig` order) plus a `get<Service>Config()` and a `with<Service>Config(...)` method that delegates to
   `updateServiceConfig(...)`.
3. Add `<Service>ConfigTest`, a `shouldWire<Service>ConfigIntoContainer()` test in
   `FlociAzContainerServicesConfigTest`, the getter in `FlociAzContainerTest.shouldDisableAllServices()` and a
   Docker-backed `services/<Service>ServiceTest`.

## Developer Certificate of Origin (DCO) sign-off

Every commit must be **signed off**, certifying the
[Developer Certificate of Origin](https://developercertificate.org/), a lightweight statement
that you wrote the contribution or otherwise have the right to submit it under the project's
license. This keeps Floci's licensing clean and unambiguous, and it is **required for a pull
request to be merged**.

Sign off by adding the `-s` flag when you commit:

```bash
git commit -s -m "feat(s3): add multipart upload copy-part support"
```

This appends a `Signed-off-by: Your Name <your@email>` trailer using your configured git
identity. If you forget, you can amend the most recent commit with `git commit --amend -s`, or
sign off a range during an interactive rebase.

### Why the DCO and not a CLA

Floci is built by the community, for the community, and the DCO is how it stays that way. There
is no agreement to sign and no rights to hand over. You certify that the work is yours to give,
you keep the copyright in it, and it reaches everyone else on the same MIT terms it arrived
under.

A CLA would ask every contributor to grant something extra to whoever holds the project. Floci
does not ask for that. The Lead Maintainer signs off the same way a first-time contributor
does, and holds no rights over your work that you do not hold over theirs. Code released under
MIT stays under MIT: free to use, fork, and build on, for anyone, permanently.

Changes to this policy are reserved to the Lead Maintainer under
[GOVERNANCE.md](https://github.com/floci-io/.github/blob/main/GOVERNANCE.md).


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

**No AI attribution.** Do not add "Generated by", "Co-Authored-By: …-bot", or similar trailers to commit messages.
Attribution should be limited to human contributors.

## Pull Request Limits and Review Bandwidth

To make sure every contribution gets a thorough, high-quality review in a reasonable time, we ask contributors to keep **no more than 2 open, non-draft pull requests** at any time in this repository.

- **Why this policy exists:** maintainer review time is limited. Capping concurrent open PRs prevents review backlogs, reduces context switching, and keeps PR cycle times short for everyone.
- **Dependent work:** if your work depends on a PR that has not been merged yet, build on that branch or note the dependency in the discussion instead of opening separate, uncoordinated PRs.
- **Draft PRs:** drafts do not count against the limit. Mark a draft as ready for review only when you have review capacity available.
- **How it is applied:** a bot labels your 3rd and later open pull requests `over-pr-limit` with a reminder. Starting **2026-10-08**, from your 5th open pull request onward, new ones are closed automatically. Your branch and commits are kept, and you can reopen the PR once one of your other PRs is merged or closed. Maintainers and dependency bots are not counted.

Once your current pull requests are reviewed, merged, or closed, you are welcome to open new ones!

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

## Reporting Security Issues

Please do **not** open public issues for security vulnerabilities. See [SECURITY.md](SECURITY.md) for how to report them privately.

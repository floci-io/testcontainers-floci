# [2.14.0](https://github.com/floci-io/testcontainers-floci/compare/v2.13.0...v2.14.0) (2026-08-15)


### Features

* Stop Floci gracefully ([45a4741](https://github.com/floci-io/testcontainers-floci/commit/45a4741fb89c36b15e7655c2adf86e21558d7a76))



# [2.13.0](https://github.com/floci-io/testcontainers-floci/compare/v2.12.0...v2.13.0) (2026-07-27)


### Bug Fixes

* **services:** Ensure multiple calls configuring the same service do not override each other. ([77cc40c](https://github.com/floci-io/testcontainers-floci/commit/77cc40c35403f3f7830adcf96e5c6b9b93c39c4e))


### Features

* **services:** Add method to disableAllServices() ([0b0c09b](https://github.com/floci-io/testcontainers-floci/commit/0b0c09b742d41d41a32ad2267209721eb86a8302))



# [2.12.0](https://github.com/floci-io/testcontainers-floci/compare/v2.11.0...v2.12.0) (2026-07-24)


### Bug Fixes

* clean up container-owned storage ([5d89985](https://github.com/floci-io/testcontainers-floci/commit/5d89985176dfd8df168ead0b399df8336f1c77bd))
* **lambda:** Fixed port selection in LambdaConfig tests as it conflicts with default MSK ports ([43ab90e](https://github.com/floci-io/testcontainers-floci/commit/43ab90eacadee3ce1132df621b3ef731dd0173af))
* **neptune:** Reduce default amount of Neptunes exposed ports to 10 (partial fix for [#250](https://github.com/floci-io/testcontainers-floci/issues/250)) ([f234b1f](https://github.com/floci-io/testcontainers-floci/commit/f234b1f291af0af389306c25eeaf04f7822e7ee1))


### Features

* **appsync:** Added support for configuration properties schemaWorkerThreads and schemaWorkerShutdownTimeoutSeconds ([bdfe752](https://github.com/floci-io/testcontainers-floci/commit/bdfe7521181e930422a3db36b2043672c7109de9))
* **cloudwatch:** Added support for configuration property queryCompletionDelayMs ([672efcf](https://github.com/floci-io/testcontainers-floci/commit/672efcf3a4bce56b723fb73aa1d77750cb1a5511))
* **ec2:** Added support for publishing security-group ports ([f65de45](https://github.com/floci-io/testcontainers-floci/commit/f65de45cb43bcaef0d19c1c90330a5c1037da05d))
* **eks:** Added support for configuration property ecrRegistryMirror ([2980907](https://github.com/floci-io/testcontainers-floci/commit/2980907d4eb1dd6f4933157d2633a94bab6b244f))
* **msk:** Added support for Kafka port range in MSK service config ([0d7321c](https://github.com/floci-io/testcontainers-floci/commit/0d7321ce9ec415f6635eef09592a91b72ddcd2b0))
* **neptune:** Added support for Neo4j config in Neptune ([6f37b5e](https://github.com/floci-io/testcontainers-floci/commit/6f37b5ec0b964af1221435ae2413ac30aed9bfd4))
* **protocols:** Added support for Floci's protocols configuration ([6c1e04f](https://github.com/floci-io/testcontainers-floci/commit/6c1e04ffbf3cdbaf1bfd9dab76d843800fd4130c))
* **rds:** Added mock-support for RDS service ([1f6af4d](https://github.com/floci-io/testcontainers-floci/commit/1f6af4d7eb6c7d6d84c71e5affeee3c9d9b81506))
* **s3:** Added support for S3 enforce auth configuration ([b6796c0](https://github.com/floci-io/testcontainers-floci/commit/b6796c0305e9e7de580980dc60bc3c75aacb23b7))
* **security:** Added support for config option corsAllowPrivateNetwork ([8cae39e](https://github.com/floci-io/testcontainers-floci/commit/8cae39e55a6bcb7505085a2f2296bfb78ae97171))
* **services:** Added support for IoT Core (+ MQTT), IoT Data Plane, Lightsail, Cloud Control, S3 Vectors, Elastic Beanstalk, CodePipeline, Amazon MQ and MemoryDB ([99ac1b1](https://github.com/floci-io/testcontainers-floci/commit/99ac1b1a49cdf1666bc82196b38e2dd45435f175))
* **tls:** Added support for configuration property awsHttpsPort ([e80ab4f](https://github.com/floci-io/testcontainers-floci/commit/e80ab4f9bc94b670cd5ec5539ac70658fc4f8495))



# [2.11.0](https://github.com/floci-io/testcontainers-floci/compare/v2.10.0...v2.11.0) (2026-06-19)


### Features

* **iam:** Added support for IAM property seedDeployerPrincipal ([2f05a08](https://github.com/floci-io/testcontainers-floci/commit/2f05a086ce7f197b37b150a00b8d5b46bfc87821))
* **services:** Added support for AWS Batch, DocumentDB, EMR, RDS Data and WAV V2. ([7a3a4ff](https://github.com/floci-io/testcontainers-floci/commit/7a3a4ffb8449f5d209f68c36e4ae9963fd3104e6))
* **sqs:** Changed default value of SQS maxMessageSize to 1048576 ([b9c2c34](https://github.com/floci-io/testcontainers-floci/commit/b9c2c345526c59dd3d82cf1b4c04849052c89c75))



# [2.10.0](https://github.com/floci-io/testcontainers-floci/compare/v2.9.0...v2.10.0) (2026-06-18)


### Features

* **cloudmap:** added support for AWS CloudMap ([78ffc57](https://github.com/floci-io/testcontainers-floci/commit/78ffc57a48b0ed815e32b68b01d17403bb28f2bd))
* **cloudtrail:** added support for AWS CloudTrail ([e65b505](https://github.com/floci-io/testcontainers-floci/commit/e65b505adf2ad47d63540541d12e68053c532b97))




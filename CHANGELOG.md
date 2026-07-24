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



# [2.9.0](https://github.com/floci-io/testcontainers-floci/compare/v2.8.0...v2.9.0) (2026-06-04)


### Bug Fixes

* ensure Optional<> is used only for return values ([35c49c2](https://github.com/floci-io/testcontainers-floci/commit/35c49c277b2f4b6ac93e6046d9cde42fb0c512c9))
* wait for startup scripts to complete ([7881a9c](https://github.com/floci-io/testcontainers-floci/commit/7881a9c2ac37121c893b50f6e84eece60a6a38c0))


### Features

* **cloud-formation:** added support for configuration of deletedStackRetentionSeconds at Cloud Formation service ([0678af6](https://github.com/floci-io/testcontainers-floci/commit/0678af60f644c2d43592a2f440cd5d1f16f824cc))
* **eks:** added support for endpointMode and IAM auth webhook (de-)activation ([be37d66](https://github.com/floci-io/testcontainers-floci/commit/be37d6652db5e5808c3adf6b6a1550ab72db4df9))
* **opensearch:** make defaultImage property optional and use Floci's default instead ([3df2df6](https://github.com/floci-io/testcontainers-floci/commit/3df2df64fdd550275a11613a47e5aab2a8c79a21))
* **services:** added support for AWS AppSync ([da5f7de](https://github.com/floci-io/testcontainers-floci/commit/da5f7de51379310ba1634421d583eaba93984a20))
* **seurity:** added support for Browser CORS security config ([ecb2c4e](https://github.com/floci-io/testcontainers-floci/commit/ecb2c4edbcd7ebb2f406f0f4fd17be49e853e603))



# [2.8.0](https://github.com/floci-io/testcontainers-floci/compare/v2.7.0...v2.8.0) (2026-05-22)


### Features

* add config support for all services available in Floci 1.5.18 ([5205b0c](https://github.com/floci-io/testcontainers-floci/commit/5205b0cf365d8c98969a27dbe2549e0532c83bc7))
* add support for default Memcached image in ElastiCache configuration ([10aedfc](https://github.com/floci-io/testcontainers-floci/commit/10aedfc14f2aa7492cb9ffc712a1282b6e328a4c))
* moved config from AthenaConfig to a dedicated DuckDB config ([358c696](https://github.com/floci-io/testcontainers-floci/commit/358c696ce71c98cfbb093e12ee1fe4798b2f81be))




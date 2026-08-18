# Changelog

## [2.15.0](https://github.com/floci-io/testcontainers-floci/compare/v2.14.0...v2.15.0) (2026-08-18)


### Features

* **config:** Add AuthConfig and InitHooksConfig ([429eeca](https://github.com/floci-io/testcontainers-floci/commit/429eeca33ba761e8eefc64971a3ba3201844ecaa))
* **config:** Add CloudHsmV2Config ([0306d3e](https://github.com/floci-io/testcontainers-floci/commit/0306d3e69a0897d4b8598a2088eee871d6b14ff9))
* **config:** Add dockerNetwork to MemoryDbConfig ([b0b1960](https://github.com/floci-io/testcontainers-floci/commit/b0b1960ef3b467a5b677c3f6c3d65616970ce3b1))
* **config:** Add GuardDutyConfig ([51f13eb](https://github.com/floci-io/testcontainers-floci/commit/51f13eb812d967ce15d5eae1e9315acb1e2faec8))
* **config:** Add maxRequestSize to ProtocolsConfig ([1a34b7f](https://github.com/floci-io/testcontainers-floci/commit/1a34b7fc5824c50abd06c827cfff60ec2a7c86b6))
* **config:** Add tickIntervalSeconds and flushRecordCount to FirehoseConfig ([60cd6ea](https://github.com/floci-io/testcontainers-floci/commit/60cd6eaaf42dc4ab2bf0130b2e48aa51b454a809))
* **config:** Add TranscribeConfig ([68dba8a](https://github.com/floci-io/testcontainers-floci/commit/68dba8ac3e4f4bb54d2ca02ba2a2fa0f99a86797))
* **protocols:** Added support for configuration property rejectUnknownServiceScope ([2e2d23d](https://github.com/floci-io/testcontainers-floci/commit/2e2d23df018037f80341683b572737c9fb107ac6))
* **services:** Add allowedPrivateOriginHosts option to CloudFront config ([f23b58f](https://github.com/floci-io/testcontainers-floci/commit/f23b58fec539c11961eeb21d5f51f3b0d9890a30))
* **services:** Add awsFaithfulPrivateIp to Ec2Config ([1c65500](https://github.com/floci-io/testcontainers-floci/commit/1c655009bfc779dbacaf3446b18e23d28903e5d2))
* **services:** Add Bedrock AgentCore and Bedrock AgentCore Control services ([d12249c](https://github.com/floci-io/testcontainers-floci/commit/d12249c561dcb63192218860bb484a50631eb291))
* **services:** Add disableCni option to EKS config ([a733556](https://github.com/floci-io/testcontainers-floci/commit/a73355630882f712cbf933edfcc5204eecc7b7b0))
* **services:** Add ecrBaseUri to LambdaConfig ([8b573c1](https://github.com/floci-io/testcontainers-floci/commit/8b573c1aa799e1eccc6fc8d38e59282473da432e))
* **services:** Add extraHosts option to Lambda config ([c0c96aa](https://github.com/floci-io/testcontainers-floci/commit/c0c96aa09e5339bfa56d004108b06e85b736a068))
* **services:** Add proxy backend options to Bedrock Runtime config ([98a951a](https://github.com/floci-io/testcontainers-floci/commit/98a951a0b21e0d2dd6f05247737c36ed73864f37))
* **services:** Added accountAlias option to IAM config ([6b1c5ce](https://github.com/floci-io/testcontainers-floci/commit/6b1c5ce59acafd02d50166964690d30cd625c3d3))
* **services:** Added allowPlaintextHttp option to Step Functions config ([f5f8103](https://github.com/floci-io/testcontainers-floci/commit/f5f810333feb59ceae6c1bde83564cc56d4d92a0))
* **services:** Added flushIntervalSeconds option to CloudTrail config ([73bc143](https://github.com/floci-io/testcontainers-floci/commit/73bc14330b5e214969dfa1dfe8b500b6aea8ec30))
* **services:** Added support for Application Auto Scaling ([cc03f9c](https://github.com/floci-io/testcontainers-floci/commit/cc03f9c5973cfb7db7a239308d99feaf31e89a6a))
* **services:** Added support for CloudWatch RUM (Real User Monitoring) ([d8279f1](https://github.com/floci-io/testcontainers-floci/commit/d8279f167ade772cfa6090bfc0de4d850ecbfbaa))
* **services:** Added support for Kinesis Analytics (Managed Service for Apache Flink) ([ff683f9](https://github.com/floci-io/testcontainers-floci/commit/ff683f9bea477d61c9f8e1aad457630776ee8746))
* **services:** Added support for MWAA (Managed Workflows for Apache Airflow) ([9681d36](https://github.com/floci-io/testcontainers-floci/commit/9681d365f9cc3784a5213e2fd4db42f5d02bdfbd))
* **services:** Added support for S3 Tables ([1cada0e](https://github.com/floci-io/testcontainers-floci/commit/1cada0e19f6fa0d4a16f9d13882ee8b2c19a8b7c))
* **services:** Added support for SWF (Simple Workflow Service) ([6281f3f](https://github.com/floci-io/testcontainers-floci/commit/6281f3f191a0f005c8e9bd71d7ccdff3f9872fb4))
* **services:** Make RDS default DB images optional, add endpointHost option ([fb7b187](https://github.com/floci-io/testcontainers-floci/commit/fb7b1877f410eb3c07bc632fc21976bbf6028535))


### Bug Fixes

* **config:** Remove keepRunningOnShutdown from MwaaConfig ([ef86706](https://github.com/floci-io/testcontainers-floci/commit/ef86706c87b84f63ec8e633a38c9e1085a0d3a7a))
* **release:** Skip snapshot-bump PRs in release-please ([26beaa8](https://github.com/floci-io/testcontainers-floci/commit/26beaa8c2f4eb732b7f8457961d4307f7b688d79))
* **release:** Skip snapshot-bump PRs in release-please ([ec70117](https://github.com/floci-io/testcontainers-floci/commit/ec701177a7c62fabf7312ac8afe5f4ff79169aa9))
* **test:** Add required networkConfiguration to AgentCore runtime creation ([d5e2dee](https://github.com/floci-io/testcontainers-floci/commit/d5e2dee25822e8da7a9c956c15bf4dc3023ea269))


### Documentation

* **lambda:** Clarify awsConfigPath credential fallback behavior ([8768546](https://github.com/floci-io/testcontainers-floci/commit/876854647a141e516bcb1a97816c62aa9111fc16))

## [2.14.0](https://github.com/floci-io/testcontainers-floci/compare/v2.13.0...v2.14.0) (2026-08-15)


### Features

* Stop Floci gracefully ([45a4741](https://github.com/floci-io/testcontainers-floci/commit/45a4741fb89c36b15e7655c2adf86e21558d7a76))



## [2.13.0](https://github.com/floci-io/testcontainers-floci/compare/v2.12.0...v2.13.0) (2026-07-27)


### Bug Fixes

* **services:** Ensure multiple calls configuring the same service do not override each other. ([77cc40c](https://github.com/floci-io/testcontainers-floci/commit/77cc40c35403f3f7830adcf96e5c6b9b93c39c4e))


### Features

* **services:** Add method to disableAllServices() ([0b0c09b](https://github.com/floci-io/testcontainers-floci/commit/0b0c09b742d41d41a32ad2267209721eb86a8302))



## [2.12.0](https://github.com/floci-io/testcontainers-floci/compare/v2.11.0...v2.12.0) (2026-07-24)


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



## [2.11.0](https://github.com/floci-io/testcontainers-floci/compare/v2.10.0...v2.11.0) (2026-06-19)


### Features

* **iam:** Added support for IAM property seedDeployerPrincipal ([2f05a08](https://github.com/floci-io/testcontainers-floci/commit/2f05a086ce7f197b37b150a00b8d5b46bfc87821))
* **services:** Added support for AWS Batch, DocumentDB, EMR, RDS Data and WAV V2. ([7a3a4ff](https://github.com/floci-io/testcontainers-floci/commit/7a3a4ffb8449f5d209f68c36e4ae9963fd3104e6))
* **sqs:** Changed default value of SQS maxMessageSize to 1048576 ([b9c2c34](https://github.com/floci-io/testcontainers-floci/commit/b9c2c345526c59dd3d82cf1b4c04849052c89c75))



## [2.10.0](https://github.com/floci-io/testcontainers-floci/compare/v2.9.0...v2.10.0) (2026-06-18)


### Features

* **cloudmap:** added support for AWS CloudMap ([78ffc57](https://github.com/floci-io/testcontainers-floci/commit/78ffc57a48b0ed815e32b68b01d17403bb28f2bd))
* **cloudtrail:** added support for AWS CloudTrail ([e65b505](https://github.com/floci-io/testcontainers-floci/commit/e65b505adf2ad47d63540541d12e68053c532b97))

# Changelog

## [2.16.1](https://github.com/floci-io/testcontainers-floci/compare/v2.16.0...v2.16.1) (2026-09-02)


### Bug Fixes

* avoid conflicts with Spring Cloud AWS Testcontainers &gt;= v4.1.0 ([d7e5a53](https://github.com/floci-io/testcontainers-floci/commit/d7e5a5321b446e9896d9b0bcbbf617295282f795))

## [2.16.0](https://github.com/floci-io/testcontainers-floci/compare/v2.15.0...v2.16.0) (2026-09-01)


### Features

* add APS (Prometheus) service support ([5a9a4e4](https://github.com/floci-io/testcontainers-floci/commit/5a9a4e48fa8d99449ea5f17d83bab022b967e483))
* add CodeGuru Reviewer service support ([a636318](https://github.com/floci-io/testcontainers-floci/commit/a636318c4a96b0af88628d483545c53945586151))
* add Comprehend service support ([943de32](https://github.com/floci-io/testcontainers-floci/commit/943de323e21001e6c713786abf453e4ee2af816c))
* add Connect service support ([aeff7e6](https://github.com/floci-io/testcontainers-floci/commit/aeff7e67d9be833bf0b802a6c3811560b2fb33bb))
* add Control Tower service support ([9fbe953](https://github.com/floci-io/testcontainers-floci/commit/9fbe9532a5171a2abd874a3febe97a4db0eb8493))
* add EFS service support ([97af513](https://github.com/floci-io/testcontainers-floci/commit/97af513f6bd03a5f43b21881ecc17cd7f24e3066))
* add EMR Serverless service support ([a9de0a3](https://github.com/floci-io/testcontainers-floci/commit/a9de0a3ca7fad15fe055d1e92880da6758f5e5f1))
* add FIS service support ([6d801fe](https://github.com/floci-io/testcontainers-floci/commit/6d801fe5bec52fc8aabee1e5f6ca0241b9207913))
* add Lake Formation service support ([b2376ab](https://github.com/floci-io/testcontainers-floci/commit/b2376ab9c201716fdcc15fefcb0c10281e26bae3))
* add Lambda containerNamePrefix config property ([f72941b](https://github.com/floci-io/testcontainers-floci/commit/f72941bb7becf11b1187eda1282d155dcab22e7b))
* add Network Firewall service support ([57939ef](https://github.com/floci-io/testcontainers-floci/commit/57939ef663127d24256a06221355495db563dfcc))
* add Organizations service support ([8ddb862](https://github.com/floci-io/testcontainers-floci/commit/8ddb8628fc88a239e8f1be246528bbec5144891a))
* add RAM service support ([2ba2d6d](https://github.com/floci-io/testcontainers-floci/commit/2ba2d6d0bdd35903400a3ec0e55989136509d11b))
* add Redshift service support ([1b1dd87](https://github.com/floci-io/testcontainers-floci/commit/1b1dd87a0d0ef7e05a04e78be0ae7c894902834c))
* add Rekognition service support ([f33d3e0](https://github.com/floci-io/testcontainers-floci/commit/f33d3e08e8214f33e98796daf0fed1e9a38c72f4))
* add Resource Explorer v2 service support ([2cf1773](https://github.com/floci-io/testcontainers-floci/commit/2cf177307992f67f12271319a8c3dd715be16adb))
* add Route 53 Resolver service support ([f51e75a](https://github.com/floci-io/testcontainers-floci/commit/f51e75aa0da7d6430b2b93cce87731466c8944ff))
* add Service Catalog service support ([a1bc222](https://github.com/floci-io/testcontainers-floci/commit/a1bc222e179c01f173447d23320b91ce352e81b3))
* add Service Quotas service support ([27431b2](https://github.com/floci-io/testcontainers-floci/commit/27431b273dac6c44e275672a5a893aa5d868eecf))
* add SSO Admin service support ([1465b7e](https://github.com/floci-io/testcontainers-floci/commit/1465b7e8b2240d8eda8f88eab8cb16441e704a6d))
* **ai:** add aiMockConfigFile config property to FlociContainer ([caeddc1](https://github.com/floci-io/testcontainers-floci/commit/caeddc10435913f1b6e5f5190086fa94f9975593))
* **cloudformation:** add allowStubLambdaCode config property ([ebb2d2d](https://github.com/floci-io/testcontainers-floci/commit/ebb2d2d60af3d88c44e79cbf7d4389d0ff81171e))
* **ec2:** add containerIpsRoutable config property ([18b605b](https://github.com/floci-io/testcontainers-floci/commit/18b605bc339ae1562ce594e89ed94c3faf484e90))
* **elasticache:** add clusterAnnounceHostname config property ([43454da](https://github.com/floci-io/testcontainers-floci/commit/43454da4abe209a83973e4bc74d816c858cfd9aa))
* **s3:** add globalBucketNamespace config property ([6faf023](https://github.com/floci-io/testcontainers-floci/commit/6faf023eacc21ea0dbe43c52bf24aef0d044992e))
* **stepfunctions:** add mockConfig for Step Functions Local mock file ([9989210](https://github.com/floci-io/testcontainers-floci/commit/9989210bb6ef6dd767a61f5b96b4563f9522dc55))
* wire Transcribe service into FlociContainer ([461986d](https://github.com/floci-io/testcontainers-floci/commit/461986d07bade2a7e8aa5381b57c477bd6a26823))


### Bug Fixes

* Added missing service config accessors for CloudFront and ConfigService ([22aac4f](https://github.com/floci-io/testcontainers-floci/commit/22aac4f19fbe5b4e3ab71358b0df07012dc9c65a))
* **lambda:** move Runtime API default port pool to 12000-12499 ([4f0706f](https://github.com/floci-io/testcontainers-floci/commit/4f0706fde28b51b71e5c3a291b84c067484c9b0f))
* mount Docker socket only when a service needs it ([02ae923](https://github.com/floci-io/testcontainers-floci/commit/02ae9236464fd2b0f17e8f6088a2c5ea9ada8aa2)), closes [#223](https://github.com/floci-io/testcontainers-floci/issues/223)
* **rds:** configure reachable endpoints ([c12394c](https://github.com/floci-io/testcontainers-floci/commit/c12394c6b30c8bf6a71b1e47a1534153ea191f2f))
* **release:** Allow manual dispatch to publish/scan an existing tag ([0381b41](https://github.com/floci-io/testcontainers-floci/commit/0381b41d476440ffc7798e2e5b002e5022fdf2e7))
* **release:** Allow manual dispatch to publish/scan an existing tag ([aaa1b7a](https://github.com/floci-io/testcontainers-floci/commit/aaa1b7a90e8d0c3659f19def11b15ee63b3ca0d1))
* update CODEOWNERS to include cfranzen ([135ddb4](https://github.com/floci-io/testcontainers-floci/commit/135ddb4b308d6c46f6ce07ffac495d52a7c8c84c))


### Documentation

* add MAINTAINERS.md and CODEOWNERS ([e116c5c](https://github.com/floci-io/testcontainers-floci/commit/e116c5c6ada8613ed82287834cf4348c3cc13436))

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

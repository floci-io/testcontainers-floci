# Changelog

## [2.16.0](https://github.com/floci-io/testcontainers-floci/compare/v2.15.0...v2.16.0) (2026-08-19)


### Features

* add config support for all services available in Floci 1.5.18 ([d28166a](https://github.com/floci-io/testcontainers-floci/commit/d28166ad7e09ede151aac873d0c884a749520e58))
* add config support for all services available in Floci 1.5.18 ([5205b0c](https://github.com/floci-io/testcontainers-floci/commit/5205b0cf365d8c98969a27dbe2549e0532c83bc7))
* add missing tests for various FlociContainer service configs ([28d4138](https://github.com/floci-io/testcontainers-floci/commit/28d413878fc06bf056362b344210423bb33f527b))
* Add support for config of Backup, Transfer Family, and Route 53 ([c8c6eea](https://github.com/floci-io/testcontainers-floci/commit/c8c6eea68fc4ac5dc23e7decef7466b28bd43a07))
* add support for default Memcached image in ElastiCache configuration ([10aedfc](https://github.com/floci-io/testcontainers-floci/commit/10aedfc14f2aa7492cb9ffc712a1282b6e328a4c))
* Add support for EC2 auto-scaling config ([9cb7c72](https://github.com/floci-io/testcontainers-floci/commit/9cb7c72c345d27eea3aab925fb09858c3bee3186))
* Add support for Lambda awsConfigPath property ([0495c9e](https://github.com/floci-io/testcontainers-floci/commit/0495c9e456729c17dfd92ead2fb30c05f5730fef))
* Add support for Textract ([f0a095c](https://github.com/floci-io/testcontainers-floci/commit/f0a095c1fcbd52e255d02cf557909f282ccacd2b))
* added configuration support for default account id and default availability zone ([4739338](https://github.com/floci-io/testcontainers-floci/commit/473933817ce66e102ba9ee26cc040484d4854357))
* added missing configuration options and services from Floci v1.5.7 ([0e01ff3](https://github.com/floci-io/testcontainers-floci/commit/0e01ff3eff4979e1930459c0f3f04a913570f499))
* added missing configuration options and services from Floci v1.5.7 ([aebc8e9](https://github.com/floci-io/testcontainers-floci/commit/aebc8e9f87506b7c0b01571eb05455534928722a))
* added support for accessing Floci via TLS ([693c8ca](https://github.com/floci-io/testcontainers-floci/commit/693c8ca603c26737e1e85ca55bece5287718228f))
* added support for configuring all services supported by Floci that don't startup child containers ([d729aef](https://github.com/floci-io/testcontainers-floci/commit/d729aefe2603836409a5882f4b9c06e34e0acdaa))
* added support for configuring all services supported by Floci that don't startup child containers ([4b4c41f](https://github.com/floci-io/testcontainers-floci/commit/4b4c41f95da3081d8e245cbfe3a28a34c4f2de4a))
* added support for EC2 starting up real containers ([f9d0c7b](https://github.com/floci-io/testcontainers-floci/commit/f9d0c7bdfaea8dd51f4577278b815b45f04fd708))
* added support for EC2 starting up real containers ([4298a60](https://github.com/floci-io/testcontainers-floci/commit/4298a6005cc4441dae832edba4d8dd7a4c11ee56))
* added support for ElastiCache, OpenSearch, ECS and ECR. ([2602584](https://github.com/floci-io/testcontainers-floci/commit/26025841498a24f83b1976d40fb154ed8f46c00c))
* added support for ElastiCache, OpenSearch, ECS and ECR. ([0f5c31a](https://github.com/floci-io/testcontainers-floci/commit/0f5c31ad6760896ef0a382c3567be62465b13123))
* added support for ELBv2 with forwarding to real load balancer ([bb5728d](https://github.com/floci-io/testcontainers-floci/commit/bb5728dd2e29e95777ac75929cc87fcda7567248))
* added support for ELBv2 with forwarding to real load balancer ([8a390a0](https://github.com/floci-io/testcontainers-floci/commit/8a390a039ebb52bb62ab613885401e12228d23d9))
* added support for Pricing Service ([368d59f](https://github.com/floci-io/testcontainers-floci/commit/368d59f69dc90b482856c6776638845445da38e4))
* added testcases for all services that are available in Floci but were not covered yet ([59fc3bc](https://github.com/floci-io/testcontainers-floci/commit/59fc3bc8f7c9a53a860074b280316126d01858e9))
* **appsync:** Added support for configuration properties schemaWorkerThreads and schemaWorkerShutdownTimeoutSeconds ([bdfe752](https://github.com/floci-io/testcontainers-floci/commit/bdfe7521181e930422a3db36b2043672c7109de9))
* **athena:** added support for floci-duck mode of AWS Athena ([9dccb06](https://github.com/floci-io/testcontainers-floci/commit/9dccb06d141a2ed212e74017245c20e716c81429))
* **cloud-formation:** added support for configuration of deletedStackRetentionSeconds at Cloud Formation service ([0678af6](https://github.com/floci-io/testcontainers-floci/commit/0678af60f644c2d43592a2f440cd5d1f16f824cc))
* **cloudmap:** added support for AWS CloudMap ([78ffc57](https://github.com/floci-io/testcontainers-floci/commit/78ffc57a48b0ed815e32b68b01d17403bb28f2bd))
* **cloudtrail:** added support for AWS CloudTrail ([e65b505](https://github.com/floci-io/testcontainers-floci/commit/e65b505adf2ad47d63540541d12e68053c532b97))
* **cloudwatch:** Added support for configuration property queryCompletionDelayMs ([672efcf](https://github.com/floci-io/testcontainers-floci/commit/672efcf3a4bce56b723fb73aa1d77750cb1a5511))
* **codebuild:** added support for dockerNetwork config for CodeBuild ([e0c7aa6](https://github.com/floci-io/testcontainers-floci/commit/e0c7aa63213631b04f6384e83a433cf0ec96fa72))
* **config:** Add AuthConfig and InitHooksConfig ([429eeca](https://github.com/floci-io/testcontainers-floci/commit/429eeca33ba761e8eefc64971a3ba3201844ecaa))
* **config:** Add CloudHsmV2Config ([0306d3e](https://github.com/floci-io/testcontainers-floci/commit/0306d3e69a0897d4b8598a2088eee871d6b14ff9))
* **config:** Add dockerNetwork to MemoryDbConfig ([b0b1960](https://github.com/floci-io/testcontainers-floci/commit/b0b1960ef3b467a5b677c3f6c3d65616970ce3b1))
* **config:** Add GuardDutyConfig ([51f13eb](https://github.com/floci-io/testcontainers-floci/commit/51f13eb812d967ce15d5eae1e9315acb1e2faec8))
* **config:** Add maxRequestSize to ProtocolsConfig ([1a34b7f](https://github.com/floci-io/testcontainers-floci/commit/1a34b7fc5824c50abd06c827cfff60ec2a7c86b6))
* **config:** Add tickIntervalSeconds and flushRecordCount to FirehoseConfig ([60cd6ea](https://github.com/floci-io/testcontainers-floci/commit/60cd6eaaf42dc4ab2bf0130b2e48aa51b454a809))
* **config:** Add TranscribeConfig ([68dba8a](https://github.com/floci-io/testcontainers-floci/commit/68dba8ac3e4f4bb54d2ca02ba2a2fa0f99a86797))
* **ec2:** Added support for publishing security-group ports ([f65de45](https://github.com/floci-io/testcontainers-floci/commit/f65de45cb43bcaef0d19c1c90330a5c1037da05d))
* **eks:** Added support for configuration property ecrRegistryMirror ([2980907](https://github.com/floci-io/testcontainers-floci/commit/2980907d4eb1dd6f4933157d2633a94bab6b244f))
* **eks:** added support for endpointMode and IAM auth webhook (de-)activation ([be37d66](https://github.com/floci-io/testcontainers-floci/commit/be37d6652db5e5808c3adf6b6a1550ab72db4df9))
* floci docker image moved ([cc639a7](https://github.com/floci-io/testcontainers-floci/commit/cc639a7588fece47465d2cfacc781e6dd67b039d))
* floci docker image moved ([0203051](https://github.com/floci-io/testcontainers-floci/commit/0203051708671897bf4b010041967efa5a074d2d))
* **iam:** Added support for IAM property seedDeployerPrincipal ([2f05a08](https://github.com/floci-io/testcontainers-floci/commit/2f05a086ce7f197b37b150a00b8d5b46bfc87821))
* initial implementation of testcontainers-floci ([effbc82](https://github.com/floci-io/testcontainers-floci/commit/effbc82beba3783c436a604e63cdd314e15da112))
* initial implementation of testcontainers-floci ([5f1305c](https://github.com/floci-io/testcontainers-floci/commit/5f1305cb5823ff6fe90793df1fa2682ad204e961))
* **lambda:** added support for Lambda hot-reload configuration ([98f9763](https://github.com/floci-io/testcontainers-floci/commit/98f976327478147690ce0e0209e511c2680691c2))
* **lambda:** added support for lambdas ([7842cff](https://github.com/floci-io/testcontainers-floci/commit/7842cffd441dc13c7d9b0b8ea37f79792c2e5438))
* **lambda:** added support for lambdas ([4ec27cd](https://github.com/floci-io/testcontainers-floci/commit/4ec27cd60c7b5905e72d4a1477319752e26a5d09))
* **logging:** allow configuration of Floci's log level ([1d9fa78](https://github.com/floci-io/testcontainers-floci/commit/1d9fa7863d59a17e1414aef9ebf1c04357d9392b))
* moved config from AthenaConfig to a dedicated DuckDB config ([358c696](https://github.com/floci-io/testcontainers-floci/commit/358c696ce71c98cfbb093e12ee1fe4798b2f81be))
* **msk:** Added support for Kafka port range in MSK service config ([0d7321c](https://github.com/floci-io/testcontainers-floci/commit/0d7321ce9ec415f6635eef09592a91b72ddcd2b0))
* **neptune:** Added support for Neo4j config in Neptune ([6f37b5e](https://github.com/floci-io/testcontainers-floci/commit/6f37b5ec0b964af1221435ae2413ac30aed9bfd4))
* **network:** added support for creating a dedicated Docker network for Floci and all its child containers ([8bae181](https://github.com/floci-io/testcontainers-floci/commit/8bae181ebdd4ee0c220bd2bae13936d6e0bea90c))
* **opensearch:** make defaultImage property optional and use Floci's default instead ([3df2df6](https://github.com/floci-io/testcontainers-floci/commit/3df2df64fdd550275a11613a47e5aab2a8c79a21))
* **protocols:** Added support for configuration property rejectUnknownServiceScope ([2e2d23d](https://github.com/floci-io/testcontainers-floci/commit/2e2d23df018037f80341683b572737c9fb107ac6))
* **protocols:** Added support for Floci's protocols configuration ([6c1e04f](https://github.com/floci-io/testcontainers-floci/commit/6c1e04ffbf3cdbaf1bfd9dab76d843800fd4130c))
* **rds:** Added mock-support for RDS service ([1f6af4d](https://github.com/floci-io/testcontainers-floci/commit/1f6af4d7eb6c7d6d84c71e5affeee3c9d9b81506))
* **rds:** added support for creating and accessing RDS instances ([8723f13](https://github.com/floci-io/testcontainers-floci/commit/8723f132757bf2a0d6360c159b82f0c0ad230fee))
* **rds:** added support for creating and accessing RDS instances ([366a732](https://github.com/floci-io/testcontainers-floci/commit/366a7329c86d852ed8c5eb3b06ead15731106d2c))
* **s3:** Added support for S3 enforce auth configuration ([b6796c0](https://github.com/floci-io/testcontainers-floci/commit/b6796c0305e9e7de580980dc60bc3c75aacb23b7))
* **security:** Added support for config option corsAllowPrivateNetwork ([8cae39e](https://github.com/floci-io/testcontainers-floci/commit/8cae39e55a6bcb7505085a2f2296bfb78ae97171))
* **services:** Add allowedPrivateOriginHosts option to CloudFront config ([f23b58f](https://github.com/floci-io/testcontainers-floci/commit/f23b58fec539c11961eeb21d5f51f3b0d9890a30))
* **services:** Add awsFaithfulPrivateIp to Ec2Config ([1c65500](https://github.com/floci-io/testcontainers-floci/commit/1c655009bfc779dbacaf3446b18e23d28903e5d2))
* **services:** Add Bedrock AgentCore and Bedrock AgentCore Control services ([d12249c](https://github.com/floci-io/testcontainers-floci/commit/d12249c561dcb63192218860bb484a50631eb291))
* **services:** Add disableCni option to EKS config ([a733556](https://github.com/floci-io/testcontainers-floci/commit/a73355630882f712cbf933edfcc5204eecc7b7b0))
* **services:** Add ecrBaseUri to LambdaConfig ([8b573c1](https://github.com/floci-io/testcontainers-floci/commit/8b573c1aa799e1eccc6fc8d38e59282473da432e))
* **services:** Add extraHosts option to Lambda config ([c0c96aa](https://github.com/floci-io/testcontainers-floci/commit/c0c96aa09e5339bfa56d004108b06e85b736a068))
* **services:** Add method to disableAllServices() ([0b0c09b](https://github.com/floci-io/testcontainers-floci/commit/0b0c09b742d41d41a32ad2267209721eb86a8302))
* **services:** Add proxy backend options to Bedrock Runtime config ([98a951a](https://github.com/floci-io/testcontainers-floci/commit/98a951a0b21e0d2dd6f05247737c36ed73864f37))
* **services:** Added accountAlias option to IAM config ([6b1c5ce](https://github.com/floci-io/testcontainers-floci/commit/6b1c5ce59acafd02d50166964690d30cd625c3d3))
* **services:** Added allowPlaintextHttp option to Step Functions config ([f5f8103](https://github.com/floci-io/testcontainers-floci/commit/f5f810333feb59ceae6c1bde83564cc56d4d92a0))
* **services:** Added flushIntervalSeconds option to CloudTrail config ([73bc143](https://github.com/floci-io/testcontainers-floci/commit/73bc14330b5e214969dfa1dfe8b500b6aea8ec30))
* **services:** added missing configuration options and services from Floci v1.5.9 ([8dc2c2a](https://github.com/floci-io/testcontainers-floci/commit/8dc2c2ad332a7618724748d6e65132e724496493))
* **services:** Added support for Application Auto Scaling ([cc03f9c](https://github.com/floci-io/testcontainers-floci/commit/cc03f9c5973cfb7db7a239308d99feaf31e89a6a))
* **services:** added support for AWS AppSync ([da5f7de](https://github.com/floci-io/testcontainers-floci/commit/da5f7de51379310ba1634421d583eaba93984a20))
* **services:** Added support for AWS Batch, DocumentDB, EMR, RDS Data and WAV V2. ([7a3a4ff](https://github.com/floci-io/testcontainers-floci/commit/7a3a4ffb8449f5d209f68c36e4ae9963fd3104e6))
* **services:** Added support for CloudWatch RUM (Real User Monitoring) ([d8279f1](https://github.com/floci-io/testcontainers-floci/commit/d8279f167ade772cfa6090bfc0de4d850ecbfbaa))
* **services:** Added support for IoT Core (+ MQTT), IoT Data Plane, Lightsail, Cloud Control, S3 Vectors, Elastic Beanstalk, CodePipeline, Amazon MQ and MemoryDB ([99ac1b1](https://github.com/floci-io/testcontainers-floci/commit/99ac1b1a49cdf1666bc82196b38e2dd45435f175))
* **services:** Added support for Kinesis Analytics (Managed Service for Apache Flink) ([ff683f9](https://github.com/floci-io/testcontainers-floci/commit/ff683f9bea477d61c9f8e1aad457630776ee8746))
* **services:** Added support for MWAA (Managed Workflows for Apache Airflow) ([9681d36](https://github.com/floci-io/testcontainers-floci/commit/9681d365f9cc3784a5213e2fd4db42f5d02bdfbd))
* **services:** Added support for S3 Tables ([1cada0e](https://github.com/floci-io/testcontainers-floci/commit/1cada0e19f6fa0d4a16f9d13882ee8b2c19a8b7c))
* **services:** Added support for SWF (Simple Workflow Service) ([6281f3f](https://github.com/floci-io/testcontainers-floci/commit/6281f3f191a0f005c8e9bd71d7ccdff3f9872fb4))
* **services:** Make RDS default DB images optional, add endpointHost option ([fb7b187](https://github.com/floci-io/testcontainers-floci/commit/fb7b1877f410eb3c07bc632fc21976bbf6028535))
* **seurity:** added support for Browser CORS security config ([ecb2c4e](https://github.com/floci-io/testcontainers-floci/commit/ecb2c4edbcd7ebb2f406f0f4fd17be49e853e603))
* **sqs:** Changed default value of SQS maxMessageSize to 1048576 ([b9c2c34](https://github.com/floci-io/testcontainers-floci/commit/b9c2c345526c59dd3d82cf1b4c04849052c89c75))
* Stop Floci gracefully ([45a4741](https://github.com/floci-io/testcontainers-floci/commit/45a4741fb89c36b15e7655c2adf86e21558d7a76))
* **tls:** Added support for configuration property awsHttpsPort ([e80ab4f](https://github.com/floci-io/testcontainers-floci/commit/e80ab4f9bc94b670cd5ec5539ac70658fc4f8495))
* updated version to 2.x to show compatibility to Spring Boot 4.0.x / Spring Cloud AWS 4.0.x / Testcontainers 2.x ([57e840e](https://github.com/floci-io/testcontainers-floci/commit/57e840e462055d7b86b643c48ccd8bdb6cd3a00e))
* use Floci's health check endpoint to consider the container to be started up ([bbbdac4](https://github.com/floci-io/testcontainers-floci/commit/bbbdac4bb8cb344d79c2cc2ed3b4d47705dfd6a3))
* use named-volumes as default for data persistence ([d0b86d5](https://github.com/floci-io/testcontainers-floci/commit/d0b86d5e54787eb3810a1ef0af18e86d4e736375))
* use named-volumes as default for data persistence ([008b4f1](https://github.com/floci-io/testcontainers-floci/commit/008b4f10c614b41697c87dc58ef2661b78137e50))


### Bug Fixes

* Athena tests are working with most recent Floci release and can be activated ([8ff8571](https://github.com/floci-io/testcontainers-floci/commit/8ff8571625a6dde512144b75edc66724493d427e))
* clean up container-owned storage ([ad3ab1f](https://github.com/floci-io/testcontainers-floci/commit/ad3ab1fc5e8b70742f7d006085a074d90e12829d))
* clean up container-owned storage ([5d89985](https://github.com/floci-io/testcontainers-floci/commit/5d89985176dfd8df168ead0b399df8336f1c77bd))
* commitlint should not fail-on-error ([085cc1b](https://github.com/floci-io/testcontainers-floci/commit/085cc1b8e715fa5224e0d1a6c28f6d6bd033e7a6))
* **config:** Remove keepRunningOnShutdown from MwaaConfig ([ef86706](https://github.com/floci-io/testcontainers-floci/commit/ef86706c87b84f63ec8e633a38c9e1085a0d3a7a))
* deactivated failing OpenSearch tests ([900b765](https://github.com/floci-io/testcontainers-floci/commit/900b765086469fd31f34c2231123ffb0ba239119))
* do auto-configuration for S3Client only if AWS S3 sdk dependency is on classpath ([45ca8b3](https://github.com/floci-io/testcontainers-floci/commit/45ca8b399122ecb778115161cd446b469e3779ef))
* ensure Optional&lt;&gt; is used only for return values ([35c49c2](https://github.com/floci-io/testcontainers-floci/commit/35c49c277b2f4b6ac93e6046d9cde42fb0c512c9))
* **lambda:** Fixed port selection in LambdaConfig tests as it conflicts with default MSK ports ([43ab90e](https://github.com/floci-io/testcontainers-floci/commit/43ab90eacadee3ce1132df621b3ef731dd0173af))
* **neptune:** Reduce default amount of Neptunes exposed ports to 10 (… ([228435a](https://github.com/floci-io/testcontainers-floci/commit/228435a26eec454f71787f8a7b377baa4751b9f7))
* **neptune:** Reduce default amount of Neptunes exposed ports to 10 (partial fix for [#250](https://github.com/floci-io/testcontainers-floci/issues/250)) ([f234b1f](https://github.com/floci-io/testcontainers-floci/commit/f234b1f291af0af389306c25eeaf04f7822e7ee1))
* OpenSearch is not exposing ports as proxy. Adopted configuration accordingly. ([3c54b96](https://github.com/floci-io/testcontainers-floci/commit/3c54b96a65ef0f6958a187c77495107f12d43c5c))
* **release:** Skip snapshot-bump PRs in release-please ([26beaa8](https://github.com/floci-io/testcontainers-floci/commit/26beaa8c2f4eb732b7f8457961d4307f7b688d79))
* **release:** Skip snapshot-bump PRs in release-please ([ec70117](https://github.com/floci-io/testcontainers-floci/commit/ec701177a7c62fabf7312ac8afe5f4ff79169aa9))
* running Floci as user root is not required any longer ([680fbe6](https://github.com/floci-io/testcontainers-floci/commit/680fbe6a3dfb257b1478a21778b73b8422ba0ae0))
* **services:** Ensure multiple calls configuring the same service do not override each other. ([77cc40c](https://github.com/floci-io/testcontainers-floci/commit/77cc40c35403f3f7830adcf96e5c6b9b93c39c4e))
* **test:** Add required networkConfiguration to AgentCore runtime creation ([d5e2dee](https://github.com/floci-io/testcontainers-floci/commit/d5e2dee25822e8da7a9c956c15bf4dc3023ea269))
* wait for startup scripts to complete ([fba1147](https://github.com/floci-io/testcontainers-floci/commit/fba1147f0d21966f61bf76f2afcbbcd01b74bb2e))
* wait for startup scripts to complete ([7881a9c](https://github.com/floci-io/testcontainers-floci/commit/7881a9c2ac37121c893b50f6e84eece60a6a38c0))


### Documentation

* add Code of Conduct to promote inclusive community standards ([d2fb909](https://github.com/floci-io/testcontainers-floci/commit/d2fb9097981eab8593387294fc7026249ebfb43a))
* add JavaDoc for default port in FlociContainer ([2c44b5c](https://github.com/floci-io/testcontainers-floci/commit/2c44b5ce537cb03fbaf26c12edd4b5fe15f045f1))
* added contributing guidelines ([3835115](https://github.com/floci-io/testcontainers-floci/commit/3835115866bc2c269346d5681c678a3736130e7a))
* added missing Java doc ([bea6074](https://github.com/floci-io/testcontainers-floci/commit/bea60743e4579fbc0682d62293056a06284d0dcd))
* added security policy ([64f6d21](https://github.com/floci-io/testcontainers-floci/commit/64f6d21abba6b7c339fef1eae69cc21184bf7b2a))
* added templates for issues and pull requests ([4334e27](https://github.com/floci-io/testcontainers-floci/commit/4334e279e5113bd81793e1310688c56070d245a0))
* Fix typo in documentation of memory DB ([e6d172d](https://github.com/floci-io/testcontainers-floci/commit/e6d172d2fe857b5570baba5def7cd5216bd91124))
* Fix typo in documentation of RDS service ([9634a00](https://github.com/floci-io/testcontainers-floci/commit/9634a005d7a17d4d9c498de8ae756becd5eeee69))
* **lambda:** Clarify awsConfigPath credential fallback behavior ([8768546](https://github.com/floci-io/testcontainers-floci/commit/876854647a141e516bcb1a97816c62aa9111fc16))
* Limit CI status badge to main branch ([74c47ad](https://github.com/floci-io/testcontainers-floci/commit/74c47ad20ef7eb7a79e08a828d2048e61e33de36))
* minor fixes to documentation ([324150c](https://github.com/floci-io/testcontainers-floci/commit/324150c01981fa8e05358043e94c6224cc0fa769))
* minor improvements to README.md ([8dbaae5](https://github.com/floci-io/testcontainers-floci/commit/8dbaae521f1d44bd6fa8e5e7bdfa07794129269e))
* removed outdated docs ([1d81c4f](https://github.com/floci-io/testcontainers-floci/commit/1d81c4f4ff0473c4c82918a9b2a44d55fc05dbce))
* show independent Maven Central badges for version 1.x and 2.x ([06aa78a](https://github.com/floci-io/testcontainers-floci/commit/06aa78a40d1a76b01cbc4abcf145ebc95a0ebe2a))
* updated Floci GitHub link in documentation after project has been moved to its own org ([cccaadf](https://github.com/floci-io/testcontainers-floci/commit/cccaadf93c62458b6543f1e4bf85319d82206ff0))
* Updated README.md with missing config options ([463c677](https://github.com/floci-io/testcontainers-floci/commit/463c6778490acb35711b1a8426f13f41ecb2bbc6))

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

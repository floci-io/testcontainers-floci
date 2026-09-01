# Changelog

## [1.15.0](https://github.com/floci-io/testcontainers-floci/compare/v1.14.0...v1.15.0) (2026-09-01)


### Features

* add APS (Prometheus) service support ([e842869](https://github.com/floci-io/testcontainers-floci/commit/e842869568bc7d7a00296a1ecf0fc579f151b447))
* add CodeGuru Reviewer service support ([594b858](https://github.com/floci-io/testcontainers-floci/commit/594b858db74a356f35a21bc4a5e1beb8422af784))
* add Comprehend service support ([3f663f5](https://github.com/floci-io/testcontainers-floci/commit/3f663f53ad53cf4b10af4bada1ed1bed8a184136))
* add Connect service support ([446e37c](https://github.com/floci-io/testcontainers-floci/commit/446e37cc8e6a31b5469cc284d45c97cd96d687ec))
* add Control Tower service support ([0e0fc72](https://github.com/floci-io/testcontainers-floci/commit/0e0fc72fbf779d5bb54909aca6ca8fd8c3399f50))
* add EFS service support ([8e816db](https://github.com/floci-io/testcontainers-floci/commit/8e816db6c10710ef3321feced8cc378fb8f52864))
* add EMR Serverless service support ([4ea48e7](https://github.com/floci-io/testcontainers-floci/commit/4ea48e7bec7f50cd93f84621aecd268bbda1ef39))
* add FIS service support ([e6ee53f](https://github.com/floci-io/testcontainers-floci/commit/e6ee53f1a5ac93472c3f506ea30923224fc04655))
* add Lake Formation service support ([59fcf0a](https://github.com/floci-io/testcontainers-floci/commit/59fcf0abb0610a56576ea8048408718c2e2db608))
* add Lambda containerNamePrefix config property ([9dccbf0](https://github.com/floci-io/testcontainers-floci/commit/9dccbf04026636b10aa3de71d7753bc0643a34ce))
* add Network Firewall service support ([a1fd9d6](https://github.com/floci-io/testcontainers-floci/commit/a1fd9d63189697b200089c5ca809a2be4066e06a))
* add Organizations service support ([7551489](https://github.com/floci-io/testcontainers-floci/commit/755148919e6f2520245a5b16506aabcc10c668f7))
* add RAM service support ([bd71c02](https://github.com/floci-io/testcontainers-floci/commit/bd71c0263690cae68a21d060e114d42b9fcd477f))
* add Redshift service support ([e1829bb](https://github.com/floci-io/testcontainers-floci/commit/e1829bb2e4a1a48956ac213a2853e0833d99598f))
* add Rekognition service support ([71b0f4e](https://github.com/floci-io/testcontainers-floci/commit/71b0f4e0f3f168d46895005772ac7d3c9ab00315))
* add Resource Explorer v2 service support ([be6a17b](https://github.com/floci-io/testcontainers-floci/commit/be6a17b8e3fb32b91bd9661db6fd7a1c190ac147))
* add Route 53 Resolver service support ([a323d78](https://github.com/floci-io/testcontainers-floci/commit/a323d7822fdfb8ff5c72367ab79fe272a604ca0f))
* add Service Catalog service support ([33dd656](https://github.com/floci-io/testcontainers-floci/commit/33dd65658421273c71caabdf08a6a17ff48389d8))
* add Service Quotas service support ([12de704](https://github.com/floci-io/testcontainers-floci/commit/12de70448f01b1f25cd8ae8075f7622bf6142912))
* add SSO Admin service support ([9a1e141](https://github.com/floci-io/testcontainers-floci/commit/9a1e14113828fac7b5ddc7ec31b4e8412b53d228))
* **ai:** add aiMockConfigFile config property to FlociContainer ([52df3a3](https://github.com/floci-io/testcontainers-floci/commit/52df3a314deb1a900c6fb24396dba8665b24c030))
* **cloudformation:** add allowStubLambdaCode config property ([9aeba3e](https://github.com/floci-io/testcontainers-floci/commit/9aeba3e0bbe86ef75bdedc2ef7fd67015652c7e8))
* **ec2:** add containerIpsRoutable config property ([75b8c15](https://github.com/floci-io/testcontainers-floci/commit/75b8c15f448b6cc3fd3a9f165239bd9132baf6b7))
* **elasticache:** add clusterAnnounceHostname config property ([9f427f3](https://github.com/floci-io/testcontainers-floci/commit/9f427f350275d93106e72af285f9c0af33f51880))
* **s3:** add globalBucketNamespace config property ([47b5475](https://github.com/floci-io/testcontainers-floci/commit/47b54753ef41d77561512ce112108f61a785e733))
* **stepfunctions:** add mockConfig for Step Functions Local mock file ([b904907](https://github.com/floci-io/testcontainers-floci/commit/b9049071a37db3432116412f05f47be9283d8739))
* wire Transcribe service into FlociContainer ([ae5f2c2](https://github.com/floci-io/testcontainers-floci/commit/ae5f2c2aee72e7a2df62b1dca37d9c2cdcc11b81))


### Bug Fixes

* Added missing service config accessors for CloudFront and ConfigService ([fae4168](https://github.com/floci-io/testcontainers-floci/commit/fae41683d1fcffbc81c1d86a77d8c111f3d5e736))
* **lambda:** move Runtime API default port pool to 12000-12499 ([8d5dce3](https://github.com/floci-io/testcontainers-floci/commit/8d5dce317ac3f5ef8380cf9f8fe281af9a37b63f))
* mount Docker socket only when a service needs it ([7345355](https://github.com/floci-io/testcontainers-floci/commit/7345355b4d5285e41353106705bfb2c2c02bae06))
* **rds:** configure reachable endpoints ([70c23cb](https://github.com/floci-io/testcontainers-floci/commit/70c23cbabbab3e4ad2ae47b8b90f7395cc48f91e))
* update CODEOWNERS to include cfranzen ([1052ebd](https://github.com/floci-io/testcontainers-floci/commit/1052ebd55ab58ef846bd7f2baab7e4920ec0a4d0))


### Documentation

* add MAINTAINERS.md and CODEOWNERS ([09ce9b0](https://github.com/floci-io/testcontainers-floci/commit/09ce9b0cc09af636bc90f31b2ecd87d3f2c07e1c))

## [1.14.0](https://github.com/floci-io/testcontainers-floci/compare/v1.13.0...v1.14.0) (2026-08-18)


### Features

* **config:** Add AuthConfig and InitHooksConfig ([4c19515](https://github.com/floci-io/testcontainers-floci/commit/4c1951565e689394e44ebe6a83cfe6b42ba8cd9e))
* **config:** Add CloudHsmV2Config ([f148cc0](https://github.com/floci-io/testcontainers-floci/commit/f148cc05077d4c8e6c8b0263d5d16dda464870ce))
* **config:** Add dockerNetwork to MemoryDbConfig ([d549250](https://github.com/floci-io/testcontainers-floci/commit/d549250fa252981a8c430a02492ac4238181dd90))
* **config:** Add GuardDutyConfig ([7321281](https://github.com/floci-io/testcontainers-floci/commit/7321281a5b8eeb097c55c4dbae10be2a927cbe0d))
* **config:** Add maxRequestSize to ProtocolsConfig ([f10f5b6](https://github.com/floci-io/testcontainers-floci/commit/f10f5b620b22f71e01855645aab05e10674aed3b))
* **config:** Add tickIntervalSeconds and flushRecordCount to FirehoseConfig ([d36d6c1](https://github.com/floci-io/testcontainers-floci/commit/d36d6c18be9480de007a0bcfa290fe82a6c17f30))
* **config:** Add TranscribeConfig ([db0d7b6](https://github.com/floci-io/testcontainers-floci/commit/db0d7b607a11ca939d4d1388292fc3fd6e5304f6))
* **protocols:** Added support for configuration property rejectUnknownServiceScope ([5508f12](https://github.com/floci-io/testcontainers-floci/commit/5508f12b218b2b5c2517005693f6173bbfc064da))
* **services:** Add allowedPrivateOriginHosts option to CloudFront config ([5125cc4](https://github.com/floci-io/testcontainers-floci/commit/5125cc48e23fbd3638966d4bd0cbbc96a8cebad9))
* **services:** Add awsFaithfulPrivateIp to Ec2Config ([5c12650](https://github.com/floci-io/testcontainers-floci/commit/5c126500445c464cc66e9d34ab10302ff36a050e))
* **services:** Add Bedrock AgentCore and Bedrock AgentCore Control services ([5a38037](https://github.com/floci-io/testcontainers-floci/commit/5a38037c929cb482a8b7633b5a2daaa79687dcde))
* **services:** Add disableCni option to EKS config ([ae0ea8d](https://github.com/floci-io/testcontainers-floci/commit/ae0ea8d762edb6e3e0f2e5287f7af4dd7e6e7909))
* **services:** Add ecrBaseUri to LambdaConfig ([60428cf](https://github.com/floci-io/testcontainers-floci/commit/60428cf752aafb54ab9736d3420ff4cf5161eba8))
* **services:** Add extraHosts option to Lambda config ([e7ca206](https://github.com/floci-io/testcontainers-floci/commit/e7ca206ca8ee647551ccac53c142a3f708f87ec4))
* **services:** Add proxy backend options to Bedrock Runtime config ([da6342a](https://github.com/floci-io/testcontainers-floci/commit/da6342abbf9719abdb3f2e22c46af83ca4a917ca))
* **services:** Added accountAlias option to IAM config ([440530b](https://github.com/floci-io/testcontainers-floci/commit/440530bc2a5ee7c05cae70407c3a0e8a940378ea))
* **services:** Added allowPlaintextHttp option to Step Functions config ([1548273](https://github.com/floci-io/testcontainers-floci/commit/1548273f14b1c4044b9ec9c35ddf9b9d07b08a03))
* **services:** Added flushIntervalSeconds option to CloudTrail config ([ec92e66](https://github.com/floci-io/testcontainers-floci/commit/ec92e66f33e869da93ef2facf4aaa7599a7219a8))
* **services:** Added support for Application Auto Scaling ([2b0cb46](https://github.com/floci-io/testcontainers-floci/commit/2b0cb4662ddb867364051fda97257a5f8a8ef024))
* **services:** Added support for CloudWatch RUM (Real User Monitoring) ([6c3cae5](https://github.com/floci-io/testcontainers-floci/commit/6c3cae5b7e5f6df16a432b6e4496c58debd98acf))
* **services:** Added support for Kinesis Analytics (Managed Service for Apache Flink) ([9be7bcd](https://github.com/floci-io/testcontainers-floci/commit/9be7bcd1b7f6d715202d8b4e7a1cd28112133f10))
* **services:** Added support for MWAA (Managed Workflows for Apache Airflow) ([5dd5285](https://github.com/floci-io/testcontainers-floci/commit/5dd528592a19e9d498d47f654ad118069e6837be))
* **services:** Added support for S3 Tables ([cd5e426](https://github.com/floci-io/testcontainers-floci/commit/cd5e4267eca138bb14a8de87606e754f54839073))
* **services:** Added support for SWF (Simple Workflow Service) ([1c82e33](https://github.com/floci-io/testcontainers-floci/commit/1c82e33f8b26bb673759a9ef3a357497e1cd365a))
* **services:** Make RDS default DB images optional, add endpointHost option ([711d2b5](https://github.com/floci-io/testcontainers-floci/commit/711d2b50a191c6353326e80e3cffa49e62f33806))


### Bug Fixes

* **config:** Remove keepRunningOnShutdown from MwaaConfig ([3e173a3](https://github.com/floci-io/testcontainers-floci/commit/3e173a3069422efcd22a1f34061a772452b46bc6))
* **release:** Skip snapshot-bump PRs in release-please ([c0bfc99](https://github.com/floci-io/testcontainers-floci/commit/c0bfc99ebf979db336e46f6525adcf37776fc170))
* **release:** Skip snapshot-bump PRs in release-please ([c6e7344](https://github.com/floci-io/testcontainers-floci/commit/c6e734404ba90dee770f0510c56e361a08c70a93))
* **test:** Add required networkConfiguration to AgentCore runtime creation ([766c0c8](https://github.com/floci-io/testcontainers-floci/commit/766c0c889c726513a19fee3b6ef9b65ad93b73a7))


### Documentation

* **lambda:** Clarify awsConfigPath credential fallback behavior ([e13fa44](https://github.com/floci-io/testcontainers-floci/commit/e13fa4455e7b4e59774e78292b59b0450caabaa0))

## [1.13.0](https://github.com/floci-io/testcontainers-floci/compare/v1.12.0...v1.13.0) (2026-08-15)


### Features

* Stop Floci gracefully ([63ddc9b](https://github.com/floci-io/testcontainers-floci/commit/63ddc9b3b49c0fccacebea92c06094acb1714712))



## [1.12.0](https://github.com/floci-io/testcontainers-floci/compare/v1.11.0...v1.12.0) (2026-07-27)


### Bug Fixes

* **services:** Ensure multiple calls configuring the same service do not override each other. ([1485f4f](https://github.com/floci-io/testcontainers-floci/commit/1485f4f8e2b8a7be01b79fd6ae6b60edcc4dcd0c))


### Features

* **services:** Add method to disableAllServices() ([76bb830](https://github.com/floci-io/testcontainers-floci/commit/76bb830f073cda780ef3b96ea21f23d31e74554a))



## [1.11.0](https://github.com/floci-io/testcontainers-floci/compare/v1.10.0...v1.11.0) (2026-07-24)


### Bug Fixes

* clean up container-owned storage ([9262a0f](https://github.com/floci-io/testcontainers-floci/commit/9262a0fe31ee26ee7f4c32340b8c3b5d80e652aa))
* **lambda:** Fixed port selection in LambdaConfig tests as it conflicts with default MSK ports ([d4b228f](https://github.com/floci-io/testcontainers-floci/commit/d4b228febb1d950c5fe0f5789e49c423bcbfc1a1))
* **neptune:** Reduce default amount of Neptunes exposed ports to 10 (partial fix for [#250](https://github.com/floci-io/testcontainers-floci/issues/250)) ([3344f44](https://github.com/floci-io/testcontainers-floci/commit/3344f44342f50507c4f7e641ce15759029782d24))


### Features

* **appsync:** Added support for configuration properties schemaWorkerThreads and schemaWorkerShutdownTimeoutSeconds ([d3157b4](https://github.com/floci-io/testcontainers-floci/commit/d3157b465d8af78b60679b15ff1325329ca7c884))
* **cloudwatch:** Added support for configuration property queryCompletionDelayMs ([7004a58](https://github.com/floci-io/testcontainers-floci/commit/7004a58378d8cd2dd2d0f74d83344ac3ed5cebfa))
* **ec2:** Added support for publishing security-group ports ([6111fa6](https://github.com/floci-io/testcontainers-floci/commit/6111fa663826ec2a6d447188ed1e777fe2476997))
* **eks:** Added support for configuration property ecrRegistryMirror ([920bbe9](https://github.com/floci-io/testcontainers-floci/commit/920bbe97b9b8c967c609683e6952a69c05a38806))
* **msk:** Added support for Kafka port range in MSK service config ([1645c2e](https://github.com/floci-io/testcontainers-floci/commit/1645c2eb2c0a3d59700b12e529cda813a014e6ee))
* **neptune:** Added support for Neo4j config in Neptune ([54904b2](https://github.com/floci-io/testcontainers-floci/commit/54904b2a225a1ab2f4c0bc20077485c12ebf7de3))
* **protocols:** Added support for Floci's protocols configuration ([79e87d5](https://github.com/floci-io/testcontainers-floci/commit/79e87d5f65b8f3bc1bc4aa5059a6f7474bc0eeb0))
* **rds:** Added mock-support for RDS service ([4d69278](https://github.com/floci-io/testcontainers-floci/commit/4d692786ac092aea14320d48a0dabe7480c42624))
* **s3:** Added support for S3 enforce auth configuration ([5169524](https://github.com/floci-io/testcontainers-floci/commit/5169524f296d9094b10668148cfb06b46d264f38))
* **security:** Added support for config option corsAllowPrivateNetwork ([5474ac1](https://github.com/floci-io/testcontainers-floci/commit/5474ac1b0cc8c40cfad9e23cac3e08fd8028c4af))
* **services:** Added support for IoT Core (+ MQTT), IoT Data Plane, Lightsail, Cloud Control, S3 Vectors, Elastic Beanstalk, CodePipeline, Amazon MQ and MemoryDB ([b2d9fd8](https://github.com/floci-io/testcontainers-floci/commit/b2d9fd89402edd8c24a8ddb55131c5ee6af54241))
* **tls:** Added support for configuration property awsHttpsPort ([52bd8ba](https://github.com/floci-io/testcontainers-floci/commit/52bd8ba689d886c561852ad778af3deee72edb1b))



## [1.10.0](https://github.com/floci-io/testcontainers-floci/compare/v1.9.0...v1.10.0) (2026-06-19)


### Features

* **iam:** Added support for IAM property seedDeployerPrincipal ([932676d](https://github.com/floci-io/testcontainers-floci/commit/932676dd6647203b54d91ee72540f227f05b0707))
* **services:** Added support for AWS Batch, DocumentDB, EMR, RDS Data and WAV V2. ([c7f7f04](https://github.com/floci-io/testcontainers-floci/commit/c7f7f04e873f193f8f327479cda25ab3a1c34a10))
* **sqs:** Changed default value of SQS maxMessageSize to 1048576 ([22b89b4](https://github.com/floci-io/testcontainers-floci/commit/22b89b44b5ee92beedba890fd8896cf6ec4f5d04))



## [1.9.0](https://github.com/floci-io/testcontainers-floci/compare/v1.8.0...v1.9.0) (2026-06-18)


### Features

* **cloudmap:** added support for AWS CloudMap ([64f41ac](https://github.com/floci-io/testcontainers-floci/commit/64f41ace0d9d4eec2582bcb94c8f340b997263a0))
* **cloudtrail:** added support for AWS CloudTrail ([7b2f67b](https://github.com/floci-io/testcontainers-floci/commit/7b2f67b2d6d19cd3e1c6163e955ae3e2e040513a))

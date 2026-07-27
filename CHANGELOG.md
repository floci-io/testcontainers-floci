# [1.12.0](https://github.com/floci-io/testcontainers-floci/compare/v1.11.0...v1.12.0) (2026-07-27)


### Bug Fixes

* **services:** Ensure multiple calls configuring the same service do not override each other. ([1485f4f](https://github.com/floci-io/testcontainers-floci/commit/1485f4f8e2b8a7be01b79fd6ae6b60edcc4dcd0c))


### Features

* **services:** Add method to disableAllServices() ([76bb830](https://github.com/floci-io/testcontainers-floci/commit/76bb830f073cda780ef3b96ea21f23d31e74554a))



# [1.11.0](https://github.com/floci-io/testcontainers-floci/compare/v1.10.0...v1.11.0) (2026-07-24)


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



# [1.10.0](https://github.com/floci-io/testcontainers-floci/compare/v1.9.0...v1.10.0) (2026-06-19)


### Features

* **iam:** Added support for IAM property seedDeployerPrincipal ([932676d](https://github.com/floci-io/testcontainers-floci/commit/932676dd6647203b54d91ee72540f227f05b0707))
* **services:** Added support for AWS Batch, DocumentDB, EMR, RDS Data and WAV V2. ([c7f7f04](https://github.com/floci-io/testcontainers-floci/commit/c7f7f04e873f193f8f327479cda25ab3a1c34a10))
* **sqs:** Changed default value of SQS maxMessageSize to 1048576 ([22b89b4](https://github.com/floci-io/testcontainers-floci/commit/22b89b44b5ee92beedba890fd8896cf6ec4f5d04))



# [1.9.0](https://github.com/floci-io/testcontainers-floci/compare/v1.8.0...v1.9.0) (2026-06-18)


### Features

* **cloudmap:** added support for AWS CloudMap ([64f41ac](https://github.com/floci-io/testcontainers-floci/commit/64f41ace0d9d4eec2582bcb94c8f340b997263a0))
* **cloudtrail:** added support for AWS CloudTrail ([7b2f67b](https://github.com/floci-io/testcontainers-floci/commit/7b2f67b2d6d19cd3e1c6163e955ae3e2e040513a))



# [1.8.0](https://github.com/floci-io/testcontainers-floci/compare/v1.7.0...v1.8.0) (2026-06-04)


### Bug Fixes

* ensure Optional<> is used only for return values ([c871a63](https://github.com/floci-io/testcontainers-floci/commit/c871a637812be4b73a48558ec15fe1a878ac7c19))
* wait for startup scripts to complete ([d7a0f56](https://github.com/floci-io/testcontainers-floci/commit/d7a0f56b28f0d0f70692a64dc035df9332692476))


### Features

* **cloud-formation:** added support for configuration of deletedStackRetentionSeconds at Cloud Formation service ([7989a70](https://github.com/floci-io/testcontainers-floci/commit/7989a7092ed9dd32e752d664fae1e76339e3e7bc))
* **eks:** added support for endpointMode and IAM auth webhook (de-)activation ([a1224ff](https://github.com/floci-io/testcontainers-floci/commit/a1224ff2d9c9e3e9d622b2e3d8e7bb2419eca5a7))
* **opensearch:** make defaultImage property optional and use Floci's default instead ([a6646d2](https://github.com/floci-io/testcontainers-floci/commit/a6646d281aaaeea9c690dbe1c6759a876094ff9f))
* **services:** added support for AWS AppSync ([98b16d1](https://github.com/floci-io/testcontainers-floci/commit/98b16d11310dac36d8a732d514b71337c0fc1852))
* **seurity:** added support for Browser CORS security config ([8829676](https://github.com/floci-io/testcontainers-floci/commit/88296769686e2168a1c9941e4292e28abf856bf0))




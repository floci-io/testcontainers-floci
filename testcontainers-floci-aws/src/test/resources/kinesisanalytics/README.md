# `flink-job.jar`

A minimal, unbounded Apache Flink streaming job used by `KinesisAnalyticsServiceTest` to exercise
Kinesis Analytics (Managed Service for Apache Flink) in real (non-mock) mode. Real mode boots an
actual Flink JobManager/TaskManager pair and submits this jar to it via Flink's REST API — a
`READY`/`RUNNING` application needs a job that Flink itself considers running, so a placeholder
JAR without a real Flink pipeline never leaves `STARTING`.

The job never completes on its own (it blocks in an infinite `SourceFunction`), which keeps the
application in `RUNNING` for as long as the test needs it — including creating a snapshot, which
requires a deployed, running job.

It must be compiled targeting **Java 11 bytecode** (`--release 11`): the `apache/flink:1.20`
image's JVM only accepts class file versions up to 55.0 (Java 11), even though this project
otherwise targets Java 17.

## Source

```java
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class MinimalFlinkJob {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.addSource(new SourceFunction<String>() {
            private volatile boolean running = true;

            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                while (running) {
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                running = false;
            }
        }).print();
        env.execute("floci-test-job");
    }
}
```

## Rebuilding

```bash
javac --release 11 -cp flink-streaming-java-1.20.1.jar:... -d classes MinimalFlinkJob.java
printf "Manifest-Version: 1.0\nMain-Class: MinimalFlinkJob\n\n" > MANIFEST.MF
jar cfm flink-job.jar MANIFEST.MF -C classes .
```

(`flink-streaming-java` and its transitive dependencies can be resolved with
`mvn dependency:build-classpath` against a throwaway `pom.xml` declaring
`org.apache.flink:flink-streaming-java:1.20.1` — it is not a dependency of this module.)

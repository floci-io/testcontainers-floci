package io.github.hectorvent.testcontainers.floci.spring;

import io.awspring.cloud.autoconfigure.core.AwsConnectionDetails;
import io.github.hectorvent.testcontainers.floci.FlociContainer;
import java.net.URI;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;

/**
 * {@link ContainerConnectionDetailsFactory} that produces {@link AwsConnectionDetails}
 * from a {@link FlociContainer}.
 *
 * <p>This factory bridges the Floci Testcontainer to Spring Cloud AWS's auto-configuration.
 * When a {@link FlociContainer} is declared as a {@code @ServiceConnection} bean, this
 * factory creates an {@link AwsConnectionDetails} bean that Spring Cloud AWS uses to
 * configure all AWS SDK clients with the correct endpoint, region, and credentials.
 *
 * <p>This factory is only loaded when Spring Cloud AWS is on the classpath.
 */
public class FlociAwsContainerConnectionDetailsFactory
        extends ContainerConnectionDetailsFactory<FlociContainer, AwsConnectionDetails> {

    FlociAwsContainerConnectionDetailsFactory() {
    }

    @Override
    protected AwsConnectionDetails getContainerConnectionDetails(
            ContainerConnectionSource<FlociContainer> source) {
        return new FlociAwsContainerConnectionDetails(source);
    }

    private static final class FlociAwsContainerConnectionDetails
            extends ContainerConnectionDetails<FlociContainer>
            implements AwsConnectionDetails {

        private FlociAwsContainerConnectionDetails(ContainerConnectionSource<FlociContainer> source) {
            super(source);
        }

        @Override
        public URI getEndpoint() {
            return URI.create(getContainer().getEndpoint());
        }

        @Override
        public String getRegion() {
            return getContainer().getRegion();
        }

        @Override
        public String getAccessKey() {
            return getContainer().getAccessKey();
        }

        @Override
        public String getSecretKey() {
            return getContainer().getSecretKey();
        }
    }

}

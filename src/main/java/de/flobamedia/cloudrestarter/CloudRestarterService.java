package de.flobamedia.cloudrestarter;

import de.flobamedia.cloudrestarter.config.CloudRestarterConfiguration;
import eu.cloudnetservice.node.ShutdownHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;

@Singleton
public class CloudRestarterService {

    private final ShutdownHandler shutdownHandler;

    @Inject
    public CloudRestarterService(
            @NonNull CloudRestarterConfiguration configuration,
            @NonNull ShutdownHandler shutdownHandler
            ) {
        this.shutdownHandler = shutdownHandler;
        System.out.println(configuration.time() + " So cool!");
        if(configuration.enabled()) {
            shutdownHandler.shutdown();
        }
    }
}

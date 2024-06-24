package de.flobamedia.cloudrestarter.node;

import de.flobamedia.cloudrestarter.CloudRestarterModule;
import de.flobamedia.cloudrestarter.CloudRestarterService;
import de.flobamedia.cloudrestarter.config.CloudRestarterConfiguration;
import eu.cloudnetservice.driver.document.Document;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;

@Singleton
public class NodeCloudRestarterManagement {

    private final CloudRestarterModule module;
    private CloudRestarterConfiguration configuration;
    private CloudRestarterService service;

    @Inject
    public NodeCloudRestarterManagement(@NonNull CloudRestarterModule module, @NonNull CloudRestarterConfiguration configuration, @NonNull CloudRestarterService service) {
        this.module = module;
        this.configuration = configuration;
        this.service = service;
    }

    public @NonNull CloudRestarterConfiguration configuration() {
        return this.configuration;
    }

    public void configuration(@NonNull CloudRestarterConfiguration configuration) {
        this.configurationSilently(configuration);
    }

    public void configurationSilently(@NonNull CloudRestarterConfiguration configuration) {
        this.configuration = configuration;
        this.module.writeConfig(Document.newJsonDocument().appendTree(configuration));
    }

    public void start() {
        this.service.start(this.configuration);
    }
}

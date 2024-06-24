package de.flobamedia.cloudrestarter;

import de.flobamedia.cloudrestarter.config.CloudRestarterConfiguration;
import de.flobamedia.cloudrestarter.node.NodeCloudRestarterManagement;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.DocumentFactory;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.driver.service.ServiceEnvironmentType;
import eu.cloudnetservice.driver.util.ModuleHelper;
import eu.cloudnetservice.node.ShutdownHandler;
import eu.cloudnetservice.node.cluster.sync.DataSyncHandler;
import eu.cloudnetservice.node.cluster.sync.DataSyncRegistry;
import eu.cloudnetservice.node.module.listener.PluginIncludeListener;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;

import java.nio.file.Files;

@Singleton
public class CloudRestarterModule extends DriverModule {


    @ModuleTask(order = 127, lifecycle = ModuleLifeCycle.LOADED)
    public void convertConfig() {
        if (Files.exists(this.configPath())) {
            // there is a config, run the conversion
            var config = this.readConfig(DocumentFactory.json()).readDocument("config");
            if (!config.empty()) {
                // rewrite the config with all settings from the old config, but in the new format
                this.writeConfig(Document.newJsonDocument().appendTree(
                        CloudRestarterConfiguration.builder()
                                .enabled(config.getBoolean("enabled"))
                                .time(config.getString("time"))
                                .build()
                ));
            }
        }
    }

    @ModuleTask(order = 1, lifecycle = ModuleLifeCycle.LOADED)
    public void initManagement(
            @NonNull DataSyncRegistry dataSyncRegistry,
            @NonNull @Named("module") InjectionLayer<?> layer
            //
    ) {
        System.out.println("Initializing management ...");
        // construct the management instance
        var management = this.readConfigAndInstantiate(
                layer,
                CloudRestarterConfiguration.class,
                () -> CloudRestarterConfiguration.builder().build(),
                NodeCloudRestarterManagement.class,
                DocumentFactory.json());

        // sync the config of the module into the cluster
        dataSyncRegistry.registerHandler(
                DataSyncHandler.<CloudRestarterConfiguration>builder()
                        .key("cloudrestarter-config")
                        .nameExtractor($ -> "CloudRestarter Config")
                        .convertObject(CloudRestarterConfiguration.class)
                        .writer(management::configuration)
                        .singletonCollector(management::configuration)
                        .currentGetter($ -> management.configuration())
                        .build());
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.RELOADING)
    public void handleReload(@NonNull NodeCloudRestarterManagement management) {
        management.configuration(this.loadConfiguration());
    }

    /*
    @ModuleTask(order = 16, lifecycle = ModuleLifeCycle.LOADED)
    public void initListeners(
            @NonNull ModuleHelper moduleHelper,
            @NonNull EventManager eventManager,
            @NonNull NodeCloudRestarterManagement management
    ) {
        // register the listeners
        //eventManager.registerListener(NodeLabyModListener.class);
        eventManager.registerListener(new PluginIncludeListener(
                "cloudnet-labymod",
                CloudRestarterModule.class,
                moduleHelper,
                service -> {
                    if (management.configuration().enabled()) {
                        return service.serviceId().environment().readProperty(ServiceEnvironmentType.JAVA_PROXY);
                    }
                    return false;
                }));
    }**/

    private @NonNull CloudRestarterConfiguration loadConfiguration() {
        return this.readConfig(
                CloudRestarterConfiguration.class,
                () -> CloudRestarterConfiguration.builder().build(),
                DocumentFactory.json());
    }
}

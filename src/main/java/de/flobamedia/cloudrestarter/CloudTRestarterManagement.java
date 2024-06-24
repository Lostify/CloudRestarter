package de.flobamedia.cloudrestarter;

import de.flobamedia.cloudrestarter.config.CloudRestarterConfiguration;
import eu.cloudnetservice.driver.network.rpc.annotation.RPCValidation;
import lombok.NonNull;

@RPCValidation
public interface CloudTRestarterManagement {

    String CLIENT_CHANNEL = "labymod3:main";
    String MODULE_CHANNEL = "labymod_internal";
    String UPDATE_CONFIG = "update_labymod_config";

    @NonNull
    CloudRestarterConfiguration configuration();

    void configuration(@NonNull CloudRestarterConfiguration configuration);

}

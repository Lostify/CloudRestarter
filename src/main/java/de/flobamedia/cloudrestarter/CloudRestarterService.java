package de.flobamedia.cloudrestarter;

import de.flobamedia.cloudrestarter.config.CloudRestarterConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
public class CloudRestarterService {

    @Inject
    public CloudRestarterService() {}

    public void start(@NonNull CloudRestarterConfiguration configuration) {
        if(configuration.enabled()) {
            System.out.println("Next restart is " + configuration.time() + ".");
            scheduleShutdown(configuration.time());
        }
    }

    private void scheduleShutdown(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime shutdownTime = LocalTime.parse(time, formatter);

        Timer timer = new Timer(true); // Daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now();
                if (now.getHour() == shutdownTime.getHour() && now.getMinute() == shutdownTime.getMinute()) {
                    System.exit(0);
                }
            }
        }, 0, 60 * 1000); // Check every minute
    }
}

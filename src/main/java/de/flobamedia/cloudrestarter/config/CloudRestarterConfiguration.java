package de.flobamedia.cloudrestarter.config;

import com.google.common.base.Preconditions;
import lombok.NonNull;

public record CloudRestarterConfiguration(boolean enabled, String time) {

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static @NonNull Builder builder(@NonNull CloudRestarterConfiguration configuration) {
        return builder()
                .enabled(configuration.enabled())
                .time(configuration.time());
    }

    public static class Builder {

        private boolean enabled = true;
        private String time = "00:00";

        public @NonNull Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public @NonNull Builder time(@NonNull String time) {
            this.time = time;
            return this;
        }

        public @NonNull CloudRestarterConfiguration build() {
            Preconditions.checkNotNull(this.time, "Missing time");

            return new CloudRestarterConfiguration(
                    this.enabled,
                    this.time);
        }
    }
}

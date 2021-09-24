package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TicksPerSecond extends MinecraftServerBinder {
    private static final String TPS_GAUGE_NAME = "minecraft.server.tps";
    private static final String TPS_GAUGE_DESCRIPTION = "Current server ticks per second";
    private static final String TPS_GAUGE_RANGE_TAG = "range";

    public TicksPerSecond(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        Gauge.builder(TPS_GAUGE_NAME, this::getTpsOneMinute)
            .description(TPS_GAUGE_DESCRIPTION)
            .tag(TPS_GAUGE_RANGE_TAG, "1m")
            .register(registry);

        Gauge.builder(TPS_GAUGE_NAME, this::getTpsFiveMinutes)
            .description(TPS_GAUGE_DESCRIPTION)
            .tag(TPS_GAUGE_RANGE_TAG, "5m")
            .register(registry);

        Gauge.builder(TPS_GAUGE_NAME, this::getTpsFifteenMinutes)
            .description(TPS_GAUGE_DESCRIPTION)
            .tag(TPS_GAUGE_RANGE_TAG, "15m")
            .register(registry);
    }

    private double getTpsOneMinute() {
        return getMinecraftServer().getTPS()[0];
    }

    private double getTpsFiveMinutes() {
        return getMinecraftServer().getTPS()[1];
    }

    private double getTpsFifteenMinutes() {
        return getMinecraftServer().getTPS()[2];
    }


}

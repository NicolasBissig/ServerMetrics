package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AverageTickTime extends MinecraftServerBinder {
    public AverageTickTime(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        Gauge.builder("minecraft.server.ticktime.average", () -> getMinecraftServer().getAverageTickTime())
            .description("Current average tick time")
            .register(registry);
    }

}

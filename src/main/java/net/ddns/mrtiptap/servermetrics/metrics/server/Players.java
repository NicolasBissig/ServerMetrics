package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class Players extends MinecraftServerBinder {
    public Players(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("minecraft.players.total", () -> getMinecraftServer().getOfflinePlayers().length)
            .description("Amount of unique players that have joined the server")
            .register(registry);

        Gauge.builder("minecraft.players.online.total",
                () -> getMinecraftServer().getOnlinePlayers().size())
            .description("Amount of players that are currently online")
            .register(registry);

        Gauge.builder("minecraft.players.online.max",
                () -> getMinecraftServer().getMaxPlayers())
            .description("Maximum amount of players that can be online")
            .register(registry);

        Gauge.builder("minecraft.players.fullness.percent", this::calculateServerFullness)
            .description("Fullness of the server, between 0 (empty) and 1.0 (full)")
            .register(registry);
    }

    private double calculateServerFullness() {
        final double online = getMinecraftServer().getOnlinePlayers().size();
        final double capacity = getMinecraftServer().getMaxPlayers();

        return online / capacity;
    }
}

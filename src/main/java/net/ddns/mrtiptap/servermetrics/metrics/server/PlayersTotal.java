package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class PlayersTotal extends MinecraftServerBinder {
    public PlayersTotal(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("minecraft.players.total", () -> getMinecraftServer().getOfflinePlayers().length)
            .description("Amount of unique players that have joined the server")
            .register(registry);
    }
}

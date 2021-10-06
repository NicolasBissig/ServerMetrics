package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.NewPlayerJoinListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class Players extends MinecraftServerBinder {
    private MeterRegistry registry;

    public Players(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        new NewPlayerJoinListener(getPlugin(), this::registerGaugeForPlayer);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        this.registry = registry;

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

        for (OfflinePlayer player : getMinecraftServer().getOfflinePlayers()) {
            registerGaugeForPlayer(player);
        }
    }

    private double calculateServerFullness() {
        final double online = getMinecraftServer().getOnlinePlayers().size();
        final double capacity = getMinecraftServer().getMaxPlayers();

        return online / capacity;
    }

    private void registerGaugeForPlayer(OfflinePlayer player) {
        final String playerName = Objects.requireNonNullElse(player.getName(), "");
        final String UUID = player.getUniqueId().toString();
        final Tags tags = Tags.of("name", playerName, "UUID", UUID);

        Gauge.builder("minecraft.player.online",
                () -> player.isOnline()? 1 : 0)
            .tags(tags)
            .description("Online state of a player")
            .register(registry);
    }
}

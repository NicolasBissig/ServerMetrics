package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;

public class PlayersTotal extends MinecraftServerBinder {
    public PlayersTotal(Server minecraftServer) {
        super(minecraftServer);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("minecraft.players.total", () -> getMinecraftServer().getOfflinePlayers().length)
            .description("Amount of unique players that have joined the server")
            .register(registry);
    }
}

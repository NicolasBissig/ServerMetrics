package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;

public class PlayersOnline extends MinecraftServerBinder {
    public PlayersOnline(Server minecraftServer) {
        super(minecraftServer);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("minecraft.players.online.total", () -> getMinecraftServer().getOnlinePlayers().size())
            .description("Amount of players that are currently online")
            .register(registry);
    }
}

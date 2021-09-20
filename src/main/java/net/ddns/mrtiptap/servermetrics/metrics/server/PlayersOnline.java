package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;

@RequiredArgsConstructor
public class PlayersOnline implements MeterBinder {
    private final Server minecraftServer;

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("minecraft.players.online", () -> minecraftServer.getOnlinePlayers().size())
            .description("Amount of players that are currently online")
            .register(registry);
    }
}

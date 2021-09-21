package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;

public class Fullness extends MinecraftServerBinder {
    public Fullness(Server minecraftServer) {
        super(minecraftServer);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
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

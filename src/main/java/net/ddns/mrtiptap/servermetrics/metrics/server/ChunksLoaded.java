package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class ChunksLoaded extends MinecraftServerBinder {
    public ChunksLoaded(Server minecraftServer) {
        super(minecraftServer);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        Gauge.builder("minecraft.server.chunksloaded.total", this::getTotalLoadedChunks)
            .description("Amount of total loaded chunks across all worlds")
            .register(registry);
    }

    private int getTotalLoadedChunks() {
        return getMinecraftServer().getWorlds().stream()
            .mapToInt(world -> world.getLoadedChunks().length)
            .sum();
    }
}

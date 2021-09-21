package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public class AverageTickTime extends MinecraftServerBinder {
    public AverageTickTime(Server minecraftServer) {
        super(minecraftServer);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        Gauge.builder("minecraft.server.ticktime.average", () -> getMinecraftServer().getAverageTickTime())
            .description("Current average tick time")
            .register(registry);

        // TODO what about getTickTimes()?
        // getMinecraftServer().getTickTimes()
    }

}

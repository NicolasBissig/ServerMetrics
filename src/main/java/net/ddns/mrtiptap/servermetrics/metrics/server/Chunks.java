package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.WorldInitListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Chunks extends MinecraftServerBinder {

    private MeterRegistry registry;

    public Chunks(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        new WorldInitListener(getPlugin(), this::registerMetricsForWorld);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        this.registry = registry;

        getMinecraftServer().getWorlds().forEach(this::registerMetricsForWorld);
    }

    private void registerMetricsForWorld(World world) {
        Gauge.builder("minecraft.server.world.chunks.loaded.total", () -> world.getLoadedChunks().length)
            .description("Amount of total loaded chunks in this world")
            .tags("world", world.getName())
            .register(registry);

        Gauge.builder("minecraft.server.world.chunks.total", world::getChunkCount)
            .description("Amount of total chunks in this world")
            .tags("world", world.getName())
            .register(registry);
    }
}

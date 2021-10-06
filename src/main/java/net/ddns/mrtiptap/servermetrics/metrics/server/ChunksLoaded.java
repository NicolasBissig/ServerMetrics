package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.WorldInitListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ChunksLoaded extends MinecraftServerBinder {

    private MeterRegistry registry;

    public ChunksLoaded(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        final WorldInitListener newWorldListener = new WorldInitListener(getPlugin(), this::registerChunksLoadedForWorld);
        getPlugin().getServer().getPluginManager().registerEvents(newWorldListener, plugin);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        this.registry = registry;
    }

    private void registerChunksLoadedForWorld(World world) {
        Gauge.builder("minecraft.server.world.chunksloaded.total", () -> world.getLoadedChunks().length)
            .description("Amount of total loaded chunks in this world")
            .tags("world", world.getName())
            .register(registry);
    }
}

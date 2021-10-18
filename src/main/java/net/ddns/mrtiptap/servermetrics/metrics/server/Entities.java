package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.WorldInitListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Entities extends MinecraftServerBinder {

    private MeterRegistry registry;

    public Entities(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        new WorldInitListener(getPlugin(), this::registerChunksLoadedForWorld);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        this.registry = registry;

        getMinecraftServer().getWorlds().forEach(this::registerChunksLoadedForWorld);
    }

    private void registerChunksLoadedForWorld(World world) {
        Gauge.builder("minecraft.server.world.entities.total", () -> {
                try {
                    final Future<Integer> amountOfEntities = getMinecraftServer().getScheduler()
                        .callSyncMethod(getPlugin(), () -> world.getEntities().size());
                    return amountOfEntities.get();
                } catch (ExecutionException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            })
            .description("Amount of total entities in this world")
            .tags("world", world.getName())
            .register(registry);
    }
}

package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MinecraftServerMetrics implements MeterBinder {
    private final ConfigurationSection configuration;

    private final Map<String, Supplier<MeterBinder>> keyToMetric = new HashMap<>();

    public MinecraftServerMetrics(Server minecraftServer, ConfigurationSection configuration) {
        this.configuration = configuration;

        keyToMetric.put("AverageTickTime", () -> new AverageTickTime(minecraftServer));
        keyToMetric.put("TicksPerSecond", () -> new TicksPerSecond(minecraftServer));
        keyToMetric.put("ChunksLoaded", () -> new ChunksLoaded(minecraftServer));
        keyToMetric.put("Fullness", () -> new Fullness(minecraftServer));
        keyToMetric.put("PlayersOnline", () -> new PlayersOnline(minecraftServer));
        keyToMetric.put("PlayersTotal", () -> new PlayersTotal(minecraftServer));
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        configuration.getKeys(false).stream()
            .filter(configuration::getBoolean)
            .map(metric -> keyToMetric.get(metric).get())
            .forEach(binder -> binder.bindTo(registry));
    }
}

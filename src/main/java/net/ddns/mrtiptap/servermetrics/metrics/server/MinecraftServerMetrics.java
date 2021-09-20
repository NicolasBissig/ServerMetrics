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

        keyToMetric.put("playersOnline", () -> new PlayersOnline(minecraftServer));
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        configuration.getKeys(false).stream()
            .map(metric -> keyToMetric.get(metric).get())
            .forEach(binder -> binder.bindTo(registry));
    }
}

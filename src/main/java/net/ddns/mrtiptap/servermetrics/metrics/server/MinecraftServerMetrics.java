package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MinecraftServerMetrics implements MeterBinder {
    private final ConfigurationSection configuration;

    private final Map<String, Function<ConfigurationSection, MinecraftServerBinder>> keyToMetric;

    public MinecraftServerMetrics(Plugin plugin, ConfigurationSection configuration) {
        this.configuration = configuration;
        keyToMetric = new HashMap<>();

        keyToMetric.put("TickTimes", c -> new TickTimes(plugin, c));
        keyToMetric.put("TicksPerSecond", c -> new TicksPerSecond(plugin, c));
        keyToMetric.put("ChunksLoaded", c -> new ChunksLoaded(plugin, c));
        keyToMetric.put("Players", c -> new Players(plugin, c));
        keyToMetric.put("PlayerStatistics", c -> new PlayerStatistics(plugin, c));
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        configuration.getKeys(false).stream()
            .filter(this::isMetricEnabled)
            .map(metricName -> keyToMetric
                .get(metricName)
                .apply(configuration.getConfigurationSection(metricName)))
            .forEach(enabledMetric -> enabledMetric.bindTo(registry));
    }

    private boolean isMetricEnabled(String key) {
        if (configuration.isConfigurationSection(key)) {
            return configuration.getConfigurationSection(key).getBoolean("enabled");
        } else {
            return configuration.getBoolean(key);
        }
    }
}

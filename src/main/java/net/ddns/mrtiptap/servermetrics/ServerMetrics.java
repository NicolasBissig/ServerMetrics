package net.ddns.mrtiptap.servermetrics;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.internal.SystemMetrics;
import net.ddns.mrtiptap.servermetrics.metrics.server.MinecraftServerMetrics;
import net.ddns.mrtiptap.servermetrics.monitoringsystems.prometheus.PrometheusSetup;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerMetrics extends JavaPlugin {
    private PrometheusSetup prometheus;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final FileConfiguration config = getConfig();

        if (config.getBoolean("enabled")) {
            final CompositeMeterRegistry registry = new CompositeMeterRegistry();

            registry.config().commonTags(new CommonTags(this).gather());

            new SystemMetrics(config.getConfigurationSection("metrics.internal")).bindTo(registry);
            new MinecraftServerMetrics(this, config.getConfigurationSection("metrics.server")).bindTo(registry);

            prometheus = new PrometheusSetup(config.getConfigurationSection("prometheus"), this);
            prometheus.setup(registry);
        } else {
            getLogger().warning("ServerMetrics is disabled and won't expose metrics!");
        }
    }


    @Override
    public void onDisable() {
        if (prometheus != null) {
            prometheus.stop();
            prometheus = null;
        }
    }
}

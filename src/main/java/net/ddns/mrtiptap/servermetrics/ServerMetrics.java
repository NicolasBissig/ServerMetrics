package net.ddns.mrtiptap.servermetrics;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.internal.SystemMetrics;
import net.ddns.mrtiptap.servermetrics.metrics.server.MinecraftServerMetrics;
import net.ddns.mrtiptap.servermetrics.monitoringsystems.prometheus.PrometheusSetup;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerMetrics extends JavaPlugin {
    private PrometheusSetup prometheus;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("enabled")) {
            final CompositeMeterRegistry registry = new CompositeMeterRegistry();

            new SystemMetrics(getConfig().getConfigurationSection("metrics.internal")).bindTo(registry);
            new MinecraftServerMetrics(this, getConfig().getConfigurationSection("metrics.server")).bindTo(registry);

            prometheus = new PrometheusSetup(getConfig().getConfigurationSection("prometheus"), this);
            prometheus.setup(registry);
        } else {
            getSLF4JLogger().info("ServerMetrics is disabled and won't expose metrics!");
        }
    }

    @Override
    public void onDisable() {
        prometheus.stop();
    }
}

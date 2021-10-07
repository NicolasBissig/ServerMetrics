package net.ddns.mrtiptap.servermetrics;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import net.ddns.mrtiptap.servermetrics.metrics.internal.SystemMetrics;
import net.ddns.mrtiptap.servermetrics.metrics.server.MinecraftServerMetrics;
import net.ddns.mrtiptap.servermetrics.metricsserver.PrometheusMetricsServer;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerMetrics extends JavaPlugin {

    private PrometheusMetricsServer metricsServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("enabled")) {
            final String endpoint = getConfig().getString("prometheus.endpoint");
            final int port = getConfig().getInt("prometheus.port");

            final CompositeMeterRegistry registry = new CompositeMeterRegistry();

            new SystemMetrics(getConfig().getConfigurationSection("metrics.internal")).bindTo(registry);
            new MinecraftServerMetrics(this, getConfig().getConfigurationSection("metrics.server")).bindTo(registry);

            metricsServer = new PrometheusMetricsServer(registry, getSLF4JLogger(), endpoint, port);
            try {
                metricsServer.start();
            } catch (Exception e) {
                getSLF4JLogger().error("Failed to start prometheus metrics server!");
                getSLF4JLogger().error(e.getLocalizedMessage());
            }
        } else {
            getLogger().info("ServerMetrics is disabled and won't expose metrics!");
        }
    }

    @Override
    public void onDisable() {
        try {
            metricsServer.close();
        } catch (Exception e) {
            getSLF4JLogger().error("Could not close prometheus jetty server: {}", e.getLocalizedMessage());
        }
    }
}

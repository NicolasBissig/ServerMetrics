package net.ddns.mrtiptap.servermetrics.monitoringsystems.prometheus;

import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.io.IOException;

public class PrometheusSetup {
    private final Plugin plugin;
    private final String endpoint;
    private final int port;
    private PrometheusMetricsServer metricsServer;

    public PrometheusSetup(ConfigurationSection configurationSection, Plugin plugin) {
        this.plugin = plugin;
        endpoint = configurationSection.getString("endpoint");
        port = configurationSection.getInt("port");
    }

    public void setup(CompositeMeterRegistry registry) {
        metricsServer = new PrometheusMetricsServer(registry, plugin.getSLF4JLogger(), endpoint, port);
        try {
            metricsServer.start();
        } catch (IOException e) {
            plugin.getSLF4JLogger().error("Failed to start prometheus metrics server!");
            plugin.getSLF4JLogger().error(e.getLocalizedMessage());
        }
    }

    public void stop() {
        metricsServer.close();
    }
}

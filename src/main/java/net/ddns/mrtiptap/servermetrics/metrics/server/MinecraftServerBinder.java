package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Getter
public abstract class MinecraftServerBinder implements MeterBinder {
    private final Plugin plugin;
    private final ConfigurationSection configuration;

    public Server getMinecraftServer() {
        return plugin.getServer();
    }

    public Logger getLogger() {
        return plugin.getLogger();
    }
}

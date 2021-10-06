package net.ddns.mrtiptap.servermetrics.metrics.listeners;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

@Getter
public abstract class AbstractListener<T> implements Listener {
    private final Plugin plugin;
    private final Consumer<T> consumer;

    public AbstractListener(Plugin plugin, Consumer<T> playerConsumer) {
        this.plugin = plugin;
        this.consumer = playerConsumer;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

}

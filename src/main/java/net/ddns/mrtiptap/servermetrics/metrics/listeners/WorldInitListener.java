package net.ddns.mrtiptap.servermetrics.metrics.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class WorldInitListener implements Listener {
    private final Plugin plugin;
    private final Consumer<World> worldConsumer;

    @EventHandler
    public void onServerTickEnd(WorldInitEvent event) {
        plugin.getSLF4JLogger().info("World init event for: {}", event.getWorld().getName());
        worldConsumer.accept(event.getWorld());
    }

}

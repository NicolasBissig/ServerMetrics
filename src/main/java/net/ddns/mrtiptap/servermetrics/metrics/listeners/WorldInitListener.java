package net.ddns.mrtiptap.servermetrics.metrics.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

public class WorldInitListener extends AbstractListener<World> {

    public WorldInitListener(Plugin plugin, Consumer<World> playerConsumer) {
        super(plugin, playerConsumer);
    }

    @EventHandler
    public void onServerTickEnd(WorldInitEvent event) {
        getConsumer().accept(event.getWorld());
    }

}

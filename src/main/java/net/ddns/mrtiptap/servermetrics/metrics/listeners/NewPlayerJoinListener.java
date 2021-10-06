package net.ddns.mrtiptap.servermetrics.metrics.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;

public class NewPlayerJoinListener extends AbstractListener<Player> {
    public NewPlayerJoinListener(Plugin plugin, Consumer<Player> playerConsumer) {
        super(plugin, playerConsumer);
    }

    @EventHandler
    public void onServerTickEnd(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            getConsumer().accept(event.getPlayer());
        }
    }

}

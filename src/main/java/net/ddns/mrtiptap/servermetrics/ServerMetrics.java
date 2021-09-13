package net.ddns.mrtiptap.servermetrics;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerMetrics extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Enabling ServerMetrics plugin");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling ServerMetrics plugin");
    }

}

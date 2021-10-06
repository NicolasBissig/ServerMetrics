package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.NewPlayerJoinListener;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class PlayerStatistics extends MinecraftServerBinder {
    private static final String GAUGE_PREFIX = "minecraft.player.statistic.";

    private MeterRegistry registry;

    public PlayerStatistics(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        new NewPlayerJoinListener(getPlugin(), this::createGaugesForPlayer);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        this.registry = registry;

        for (OfflinePlayer player : getMinecraftServer().getOfflinePlayers()) {
            createGaugesForPlayer(player);
        }
    }

    private void createGaugesForPlayer(OfflinePlayer player) {
        final String playerName = Objects.requireNonNullElse(player.getName(), "");
        final String UUID = player.getUniqueId().toString();
        final Tags tags = Tags.of("name", playerName, "UUID", UUID);

        for (Statistic statistic : Statistic.values()) {
            final String statName = toValidMicroMeterKey(statistic.name());

            final Statistic.Type type = statistic.getType();

            if (type == Statistic.Type.UNTYPED) {
                Gauge.builder(GAUGE_PREFIX + statName, () -> player.getStatistic(statistic))
                    .tags(tags)
                    .tag("type", "untyped")
                    .register(registry);
            } else if (type == Statistic.Type.BLOCK) {
                for (Material block : Material.values()) {
                    final String blockKey = toValidMicroMeterKey(block.name());
                    Gauge.builder(GAUGE_PREFIX + statName + "." + blockKey, () -> player.getStatistic(statistic, block))
                        .tags(tags)
                        .tag("type", "block")
                        .register(registry);
                }
            } else if (type == Statistic.Type.ITEM) {
                for (Material block : Material.values()) {
                    final String blockKey = toValidMicroMeterKey(block.name());
                    Gauge.builder(GAUGE_PREFIX + statName + "." + blockKey, () -> player.getStatistic(statistic, block))
                        .tags(tags)
                        .tag("type", "item")
                        .register(registry);
                }
            } else if (type == Statistic.Type.ENTITY) {
                for (EntityType entity : EntityType.values()) {
                    if (entity != EntityType.UNKNOWN) {
                        final String entityKey = toValidMicroMeterKey(entity.name());
                        Gauge.builder(GAUGE_PREFIX + statName + "." + entityKey, () -> player.getStatistic(statistic, entity))
                            .tags(tags)
                            .tag("type", "entity")
                            .register(registry);
                    }
                }
            }
        }
    }

    private String toValidMicroMeterKey(String input) {
        return input.toLowerCase().replace("_", ".");
    }
}

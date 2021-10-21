package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import net.ddns.mrtiptap.servermetrics.metrics.listeners.WorldInitListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class WorldSize extends MinecraftServerBinder {
    private MeterRegistry registry;

    public WorldSize(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);

        new WorldInitListener(plugin, this::createSizeGauge);
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        this.registry = registry;

        getPlugin().getServer().getWorlds().forEach(this::createSizeGauge);
    }

    private void createSizeGauge(World world) {
        final Set<Tag> tags = new HashSet<>();
        tags.add(Tag.of("world", world.getName()));

        try {
            tags.add(Tag.of("folder", world.getWorldFolder().getCanonicalPath()));
        } catch (IOException e) {
            getLogger().warning("Cannot get path of world: " + world.getName() + ": " + e.getMessage());
        }

        Gauge.builder("minecraft.server.world.size.bytes", () -> getRealSize(world.getWorldFolder()))
            .description("Size of world in bytes")
            .tags(tags)
            .register(registry);
    }

    private long getRealSize(File folderOrFile) {
        if (folderOrFile.isDirectory()) {
            try (final Stream<Path> paths = Files.find(
                folderOrFile.toPath(), Integer.MAX_VALUE,
                (path, basicFileAttributes) -> basicFileAttributes.isRegularFile())) {

                return paths.mapToLong(path -> path.toFile().length()).sum();

            } catch (IOException e) {
                return 0;
            }
        } else {
            return folderOrFile.length();
        }
    }
}

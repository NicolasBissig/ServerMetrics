package net.ddns.mrtiptap.servermetrics;

import io.micrometer.core.instrument.Tag;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommonTags {

    private static final String TAG_SECTION = "tags";
    private static final String CUSTOM_TAG_SECTION = "custom-tags";

    private final Server server;
    private final Logger logger;
    private final ConfigurationSection tagConfig;
    private final Set<Tag> commonTags;

    public CommonTags(Plugin plugin) {
        server = plugin.getServer();
        logger = plugin.getLogger();

        tagConfig = plugin.getConfig().getConfigurationSection(TAG_SECTION);

        commonTags = new HashSet<>();
    }

    public Set<Tag> gather() {
        createTagIfEnabled("ip", server::getIp);
        createTagIfEnabled("port", server::getPort);
        createTagIfEnabled("name", server::getName);
        createTagIfEnabled("version", server::getVersion);
        createTagIfEnabled("bukkit-version", server::getBukkitVersion);
        createTagIfEnabled("motd", server::getMotd);

        final ConfigurationSection customTags = tagConfig.getConfigurationSection(CUSTOM_TAG_SECTION);

        if (customTags != null) {
            final Set<String> customKeys = customTags.getKeys(false);

            commonTags.addAll(customKeys.stream()
                .filter(key -> customTags.getString(key) != null)
                .map(key -> Tag.of(key, customTags.getString(key)))
                .collect(Collectors.toSet()));
        }

        return commonTags;
    }

    private void createTagIfEnabled(String key, Callable<Object> value) {
        try {
            final String concreteValue = value.call().toString();
            if (!concreteValue.equals("")) {
                commonTags.add(Tag.of(key, concreteValue));
            } else {
                logger.warning("Cannot create common tag " + key + ": value is not defined");
            }
        } catch (Exception e) {
            logger.warning("Cannot create common tag " + key + ": " + e.getMessage());
        }
    }
}

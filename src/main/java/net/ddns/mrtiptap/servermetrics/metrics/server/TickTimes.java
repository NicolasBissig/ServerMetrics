package net.ddns.mrtiptap.servermetrics.metrics.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import net.ddns.mrtiptap.servermetrics.ticktime.TickInfo;
import net.ddns.mrtiptap.servermetrics.ticktime.TickTimeRecorder;
import net.ddns.mrtiptap.util.Cacher;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

public class TickTimes extends MinecraftServerBinder {
    private static final String RANGES_KEY = "ranges";
    private static final String MSPT_GAUGE_NAME = "minecraft.server.mspt";
    private static final String MIN_GAUGE_NAME = MSPT_GAUGE_NAME + ".minimum";
    private static final String AVG_GAUGE_NAME = MSPT_GAUGE_NAME + ".average";
    private static final String MAX_GAUGE_NAME = MSPT_GAUGE_NAME + ".maximum";
    private static final String MSPT_GAUGE_DESCRIPTION = "Milliseconds per tick";
    private static final String MSPT_GAUGE_RANGE_TAG = "range";


    public TickTimes(Plugin plugin, ConfigurationSection configuration) {
        super(plugin, configuration);
    }

    @Override
    public void bindTo(@NotNull MeterRegistry registry) {
        final List<Integer> ranges = getConfiguration().getIntegerList(RANGES_KEY);
        final int maxRange = Collections.max(ranges);

        final TickTimeRecorder tickTimeRecorder = new TickTimeRecorder(getPlugin(), maxRange, ranges);

        final Cacher<List<TickInfo>> cache = new Cacher<>(
            tickTimeRecorder::getTickDurationInfo,
            ranges.size() * 3);

        for (int i = 0; i < ranges.size(); i++) {
            final String rangeText = String.valueOf(ranges.get(i));

            int rangeIndex = i;
            Gauge.builder(MIN_GAUGE_NAME, () -> cache.get().get(rangeIndex).getMinimum())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(MSPT_GAUGE_RANGE_TAG, rangeText)
                .register(registry);

            Gauge.builder(AVG_GAUGE_NAME, () -> cache.get().get(rangeIndex).getAverage())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(MSPT_GAUGE_RANGE_TAG, rangeText)
                .register(registry);

            Gauge.builder(MAX_GAUGE_NAME, () -> cache.get().get(rangeIndex).getMaximum())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(MSPT_GAUGE_RANGE_TAG, rangeText)
                .register(registry);
        }
    }


}

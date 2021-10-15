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
    private static final String TPS_GAUGE_NAME = "minecraft.server.tps";
    private static final String MSPT_GAUGE_NAME = "minecraft.server.mspt";
    private static final String MIN_GAUGE_NAME = MSPT_GAUGE_NAME + ".minimum";
    private static final String AVG_GAUGE_NAME = MSPT_GAUGE_NAME + ".average";
    private static final String MAX_GAUGE_NAME = MSPT_GAUGE_NAME + ".maximum";
    private static final String MSPT_GAUGE_DESCRIPTION = "Milliseconds per tick";
    private static final String TPS_GAUGE_DESCRIPTION = "Ticks per second";
    private static final String RANGE_KEY = "range";


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
            ranges.size() * 4);

        for (int i = 0; i < ranges.size(); i++) {
            final int range = ranges.get(i);
            final String rangeText = String.valueOf(range);

            int rangeIndex = i;
            Gauge.builder(MIN_GAUGE_NAME, () -> cache.get().get(rangeIndex).getMinimum())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(RANGE_KEY, rangeText)
                .register(registry);

            Gauge.builder(AVG_GAUGE_NAME, () -> cache.get().get(rangeIndex).getAverage())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(RANGE_KEY, rangeText)
                .register(registry);

            Gauge.builder(MAX_GAUGE_NAME, () -> cache.get().get(rangeIndex).getMaximum())
                .description(MSPT_GAUGE_DESCRIPTION)
                .tag(RANGE_KEY, rangeText)
                .register(registry);

            Gauge.builder(TPS_GAUGE_NAME,
                    () -> sumToTps(range, cache.get().get(rangeIndex).getDurationSum()))
                .description(TPS_GAUGE_DESCRIPTION)
                .tag(RANGE_KEY, rangeText)
                .register(registry);
        }
    }

    private double sumToTps(int amountOfTicks, double sumOfDurations) {
        final double maxTps = 20.0;
        final double maxMspt = 1000.0 / maxTps; // 50 ms per tick is okay, above not

        final double maxExpectedDuration = amountOfTicks * maxMspt;
        final double theoreticalTps = maxTps / (sumOfDurations / maxExpectedDuration);

        return Math.min(theoreticalTps, maxTps); // TPS is capped at 20
    }


}

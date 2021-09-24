package net.ddns.mrtiptap.servermetrics.ticktime;

import net.ddns.mrtiptap.util.FixedSizeList;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TickTimeRecorder {

    private final FixedSizeList<Double> tickDurations;
    private final List<Integer> ranges;

    public TickTimeRecorder(Plugin plugin, int ticksToRecord, List<Integer> ranges) {
        this.ranges = ranges;
        tickDurations = new FixedSizeList<>(ticksToRecord);

        final TickEndListener tickEndListener = new TickEndListener(this);

        if (Objects.nonNull(plugin)) {
            plugin.getServer().getPluginManager().registerEvents(tickEndListener, plugin);
        }
    }

    public void recordTickTime(double duration) {
        tickDurations.push(duration);
    }

    public List<TickInfo> getTickDurationInfo() {
        final List<TickInfo> tickInfoRanges = new ArrayList<>();
        int recordedTicks = tickDurations.size();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        int currentRange = 0;

        int i = 0;
        for (double duration : tickDurations) {
            if (currentRange < ranges.size() && i == ranges.get(currentRange)) {
                final double average = sum / Math.max(1, i);

                tickInfoRanges.add(new TickInfo(min, average, max));
                currentRange++;
            }

            min = Math.min(min, duration);
            max = Math.max(max, duration);

            sum += duration;
            i++;
        }

        final double average = sum / Math.max(1, recordedTicks);
        tickInfoRanges.add(new TickInfo(min, average, max));

        while (tickInfoRanges.size() < ranges.size()) {
            tickInfoRanges.add(tickInfoRanges.get(tickInfoRanges.size() - 1));
        }

        return tickInfoRanges;
    }
}

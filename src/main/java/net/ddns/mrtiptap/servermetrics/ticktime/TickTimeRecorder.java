package net.ddns.mrtiptap.servermetrics.ticktime;

import net.ddns.mrtiptap.util.FixedSizeList;
import java.util.ArrayList;
import java.util.List;

public class TickTimeRecorder {

    private final int ticksToRecord;
    private final FixedSizeList<Double> tickDurations;

    public TickTimeRecorder(int ticksToRecord) {
        this.ticksToRecord = ticksToRecord;
        tickDurations = new FixedSizeList<>(ticksToRecord);
    }

    public void recordTickTime(double duration) {
        tickDurations.push(duration);
    }

    public List<TickInfo> getTickDurationInfo(int... ranges) {
        final List<TickInfo> tickInfoRanges = new ArrayList<>();
        int recordedTicks = tickDurations.size();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        int currentRange = 0;

        int i = 0;
        for (double duration : tickDurations) {
            if (currentRange < ranges.length && i == ranges[currentRange]) {
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

        return tickInfoRanges;
    }
}

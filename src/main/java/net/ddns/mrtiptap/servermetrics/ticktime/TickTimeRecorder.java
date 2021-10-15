package net.ddns.mrtiptap.servermetrics.ticktime;

import net.ddns.mrtiptap.util.FixedSizeList;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TickTimeRecorder {

    private static final String ERROR_PREFIX = "Error setting up TickTimeRecorder: ";
    private static final double NS_PER_MS = 1_000_000.0;

    private final Plugin plugin;
    private final FixedSizeList<Long> tickDurations;
    private final List<Integer> ranges;

    private final long[] tickDurationArray;

    public TickTimeRecorder(Plugin plugin, int ticksToRecord, List<Integer> ranges) {
        this.plugin = plugin;
        this.ranges = ranges;

        tickDurationArray = findTickDurationArray();
        tickDurations = new FixedSizeList<>(ticksToRecord);

        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::onTick, 0, 0);
    }

    /**
     * Constructor for testing
     */
    TickTimeRecorder(int ticksToRecord, List<Integer> ranges) {
        this.plugin = null;
        this.ranges = ranges;
        this.tickDurationArray = null;

        tickDurations = new FixedSizeList<>(ticksToRecord);
    }

    /**
     * Minecraft internally tracks tick times, but the spigot api does not expose these tick times.
     * They can be found via reflection when searching for a long array.
     *
     * @return The reference of the tick times array. This array usually contains the duration
     *     of the last 100 ticks in nanoseconds.
     */
    private long[] findTickDurationArray() {
        long[] longestArray = null;

        try {
            final Server server = plugin.getServer();
            final Method getServerMethod = server.getClass().getMethod("getServer");
            final Object minecraftServer = getServerMethod.invoke(server);

            for (Field field : minecraftServer.getClass().getSuperclass().getFields()) {
                try {
                    final Class<?> type = field.getType();
                    if (type.isArray() && type.componentType().equals(long.class)) {
                        final long[] array = (long[]) field.get(minecraftServer);

                        if (array != null && (longestArray == null || array.length > longestArray.length)) {
                            longestArray = array;
                        }

                    }
                } catch (Exception ignored) {
                    // ignore
                }
            }

        } catch (NoSuchMethodException e) {
            plugin.getLogger()
                .severe(ERROR_PREFIX + "Method getServer does not exist: " + e.getMessage());
        } catch (InvocationTargetException | IllegalAccessException e) {
            plugin.getLogger()
                .severe(ERROR_PREFIX + "Cannot invoke getServer method on server: " + e.getMessage());
        }

        if (longestArray == null) {
            throw new IllegalStateException(ERROR_PREFIX + "No array of type long could be found");
        }

        return longestArray;
    }

    private void onTick() {
        final long duration = tickDurationArray[0];
        if (duration > 0) { // otherwise, a lot of 0 duration ticks would be recorded on server start
            recordTickTime(duration);
        }
    }

    void recordTickTime(long duration) {
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

                tickInfoRanges.add(new TickInfo(
                    min / NS_PER_MS,
                    average / NS_PER_MS,
                    max / NS_PER_MS,
                    sum / NS_PER_MS));
                currentRange++;
            }

            min = Math.min(min, duration);
            max = Math.max(max, duration);

            sum += duration;
            i++;
        }

        final double average = sum / Math.max(1, recordedTicks);
        tickInfoRanges.add(new TickInfo(
            min / NS_PER_MS,
            average / NS_PER_MS,
            max / NS_PER_MS,
            sum / NS_PER_MS));

        while (tickInfoRanges.size() < ranges.size()) {
            tickInfoRanges.add(tickInfoRanges.get(tickInfoRanges.size() - 1));
        }

        return tickInfoRanges;
    }
}

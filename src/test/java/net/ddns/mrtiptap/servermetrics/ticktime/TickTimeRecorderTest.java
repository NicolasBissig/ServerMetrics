package net.ddns.mrtiptap.servermetrics.ticktime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TickTimeRecorderTest {

    private final static double DELTA = 0.0000000001;

    private List<Double> generateRandomNumbers(int count) {
        final Random random = new Random();
        final List<Double> numbers = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            numbers.add(random.nextDouble());
        }

        return numbers;
    }

    private TickInfo toTickInfo(List<Double> input) {
        final double min = Collections.min(input);
        final double max = Collections.max(input);
        final double avg = input.stream()
            .mapToDouble(d -> d)
            .average()
            .orElseThrow();

        return new TickInfo(min, avg, max);
    }

    private void assertOkay(TickInfo expected, TickInfo actual) {
        assertEquals(expected.getMinimum(), actual.getMinimum(), DELTA);
        assertEquals(expected.getMaximum(), actual.getMaximum(), DELTA);
        assertEquals(expected.getAverage(), actual.getAverage(), DELTA);
    }

    @Test
    public void shouldCalculateCorrectAbsoluteTickTimes() {
        final int n = 10000;
        final TickTimeRecorder recorder = new TickTimeRecorder(null, n, List.of(n));

        final List<Double> numbers = generateRandomNumbers(n);
        final TickInfo expected = toTickInfo(numbers);

        numbers.forEach(recorder::recordTickTime);
        final TickInfo actual = recorder.getTickDurationInfo().get(0);

        assertOkay(expected, actual);
    }

    @Test
    public void shouldCalculateCorrectRangeTickTimes() {
        final int n = 20;
        final double step = 1;

        final List<Double> numbers = new ArrayList<>();
        numbers.add(step);

        for (int i = 1; i < n; i++) {
            numbers.add(numbers.get(i - 1) + step);
        }

        final List<Integer> ranges = List.of(n / 4, n / 2, n);

        final TickTimeRecorder recorder = new TickTimeRecorder(null, n, ranges);
        numbers.forEach(recorder::recordTickTime);

        Collections.reverse(numbers);

        final List<Double> quarterList = numbers.subList(0, n / 4);
        final List<Double> halfList = numbers.subList(0, n / 2);

        final TickInfo expectedQuarter = toTickInfo(quarterList);
        final TickInfo expectedHalf = toTickInfo(halfList);
        final TickInfo expected = toTickInfo(numbers);

        final List<TickInfo> actual = recorder.getTickDurationInfo();

        assertOkay(expected, actual.get(2));
        assertOkay(expectedHalf, actual.get(1));
        assertOkay(expectedQuarter, actual.get(0));
    }

    @Test
    public void shouldCalculateManyRangesCorrectly() {
        final int n = 1_000_000;
        final int ranges = 10;
        final List<Integer> concreteRanges = new ArrayList<>();

        for (int i = 0; i < ranges; i++) {
            final int currentRange = (n / ranges) * (i + 1);
            concreteRanges.add(currentRange);

        }

        final List<Double> numbers = generateRandomNumbers(n);

        final TickTimeRecorder recorder = new TickTimeRecorder(null, n, concreteRanges);
        numbers.forEach(recorder::recordTickTime);

        Collections.reverse(numbers);

        final List<TickInfo> expected = new ArrayList<>();


        for (int i = 0; i < ranges; i++) {
            final int currentRange = concreteRanges.get(i);
            final List<Double> currentValues = numbers.subList(0, currentRange);
            final TickInfo currentExpected = toTickInfo(currentValues);

            expected.add(currentExpected);
        }

        final List<TickInfo> actual = recorder.getTickDurationInfo();

        for (int i = 0; i < ranges; i++) {
            assertOkay(expected.get(i), actual.get(i));
        }
    }
}
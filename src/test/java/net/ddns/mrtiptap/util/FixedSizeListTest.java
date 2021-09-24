package net.ddns.mrtiptap.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class FixedSizeListTest {

    private FixedSizeList<Integer> fixedSizeList(int capacity) {
        return new FixedSizeList<>(capacity);
    }

    private <T> void pushElements(FixedSizeList<T> list, List<T> elements) {
        for (T element : elements) {
            list.push(element);
        }
    }

    private <T> void assertElementsAre(FixedSizeList<T> list, List<T> elements) {
        if (list.size() != elements.size()) {
            Assert.fail("List does not have the requested amount of elements: " +
                "is " + list.size() + ", expected: " + elements.size());
        } else {
            int i = 0;
            for (T element : list) {
                assertEquals(elements.get(i), element);
                i++;
            }
        }
    }

    private <T> List<T> reverse(List<T> input) {
        final List<T> copy = new ArrayList<>(input);
        Collections.reverse(copy);
        return copy;
    }

    @Test
    public void shouldHandleSimpleList() {
        final List<Integer> numbers = List.of(1, 2, 3);
        final FixedSizeList<Integer> fixedSizeList = fixedSizeList(numbers.size());

        assertEquals(numbers.size(), fixedSizeList.getMaximumSize());
        assertEquals(0, fixedSizeList.size());

        pushElements(fixedSizeList, numbers);

        assertElementsAre(fixedSizeList, reverse(numbers));
        assertEquals(numbers.size(), fixedSizeList.size());
    }

    @Test
    public void shouldPushOutOldElements() {
        final List<Integer> numbers = List.of(1, 2, 3);
        final FixedSizeList<Integer> fixedSizeList = fixedSizeList(numbers.size());

        pushElements(fixedSizeList, numbers);
        assertElementsAre(fixedSizeList, reverse(numbers));

        final List<Integer> newNumbers = List.of(4, 5, 6);

        pushElements(fixedSizeList, newNumbers);
        assertElementsAre(fixedSizeList, reverse(newNumbers));
    }

    @Test
    public void shouldPushOutOldElementsOneByOne() {
        final List<Integer> numbers = IntStream.range(0, 100).boxed().toList();
        final FixedSizeList<Integer> fixedSizeList = fixedSizeList(1);

        for (Integer number : numbers) {
            fixedSizeList.push(number);
            assertElementsAre(fixedSizeList, List.of(number));
        }
    }

}
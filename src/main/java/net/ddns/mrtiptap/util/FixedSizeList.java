package net.ddns.mrtiptap.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import java.util.LinkedList;

@ToString
@EqualsAndHashCode
public class FixedSizeList<T> implements Iterable<T> {

    @Getter
    private final int maximumSize;
    private final LinkedList<T> elements;

    public FixedSizeList(int maximumSize) {
        this.maximumSize = maximumSize;
        elements = new LinkedList<>();
    }

    public void push(T newElement) {
        final int currentSize = elements.size();

        if (currentSize + 1 > maximumSize) {
            elements.removeLast();
        }
        elements.push(newElement);
    }

    public int size() {
        return elements.size();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
}

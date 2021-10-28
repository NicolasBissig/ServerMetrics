package net.ddns.mrtiptap.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

@ToString
@EqualsAndHashCode
public class FixedSizeList<T> implements Iterable<T> {

    @Getter
    private final int maximumSize;
    private final ConcurrentLinkedDeque<T> elements;
    private int size = 0;

    public FixedSizeList(int maximumSize) {
        this.maximumSize = maximumSize;
        elements = new ConcurrentLinkedDeque<>();
    }

    public void push(T newElement) {
        if (size + 1 > maximumSize) {
            elements.removeLast();
        } else {
            size++;
        }
        elements.push(newElement);
    }

    public int size() {
        return size;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }
}

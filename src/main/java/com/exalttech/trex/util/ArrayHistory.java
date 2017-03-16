package com.exalttech.trex.util;

import java.util.function.Consumer;


public class ArrayHistory<T> {
    private Object[] history;
    private int historyLength;
    private int currentLast;
    private int capacity;

    public ArrayHistory(int historyLength) {
        this.historyLength = historyLength;
        history = new Object[this.historyLength];
        currentLast = 0;
        capacity = 0;
    }

    public T get(int index) {
        int required = currentLast + index;
        if (required < capacity) {
            return (T) history[required];
        }
        return (T) history[required - capacity];
    }

    public void add(T item) {
        if (currentLast >= historyLength) {
            currentLast = 0;
        }
        history[currentLast++] = item;
        if (capacity < historyLength) {
            capacity++;
        }
    }

    public void forEach(Consumer<? super T> action) {
        for (int i = capacity, current = currentLast; i > 0; i--) {
            if (current >= capacity) {
                current = 0;
            }
            action.accept((T) history[current++]);
        }
    }

    public int size() {
        return capacity;
    }

    public boolean isEmpty() { return capacity == 0; }

    public T last() {
        return !isEmpty() ? get(size() - 1) : null;
    }
}

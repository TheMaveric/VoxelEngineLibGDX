package com.kushagra.game.engine;

import java.util.ArrayDeque;

class ObjectPool<T> {
    private final int capacity;
    private final ObjectFactory<T> factory;
    private final ArrayDeque<T> pool;

    public ObjectPool(int capacity, ObjectFactory<T> factory) {
        this.capacity = capacity;
        this.factory = factory;
        this.pool = new ArrayDeque<>(capacity);
    }

    public T obtain() {
        T obj = pool.pollLast();
        if (obj == null) {
            obj = factory.create();
        }
        return obj;
    }

    public void free(T obj) {
        if (pool.size() < capacity) {
            pool.offerLast(obj);
        }
    }
}

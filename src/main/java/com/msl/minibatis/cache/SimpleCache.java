package com.msl.minibatis.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author msl
 * @Date 2021-01-18 22:25
 */
public class SimpleCache implements CacheFactory {

    Map<Integer, Object> cache = new HashMap<>();

    @Override
    public boolean containsKey(int key) {
        return cache.containsKey(key);
    }

    @Override
    public Object get(int key) {
        return cache.get(key);
    }

    @Override
    public void put(int key, Object value) {
        cache.put(key, value);
    }
}

package com.msl.minibatis.cache;

/**
 * @Author msl
 * @Date 2021-01-18 22:21
 */
public interface CacheFactory {

    boolean containsKey(int key);

    Object get(int key);

    void put(int key, Object value);
}

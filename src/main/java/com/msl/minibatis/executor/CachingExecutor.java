package com.msl.minibatis.executor;

import com.msl.minibatis.cache.CacheFactory;
import com.msl.minibatis.cache.CacheKey;
import com.msl.minibatis.cache.LRUCache;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author msl
 * @Date 2021-01-17 21:17
 */
@Slf4j
public class CachingExecutor implements Executor {

    private Executor delegate;

    /**
     * 一级缓存
     */
    CacheFactory cache = new LRUCache(16);

    public CachingExecutor(Executor delegate, CacheFactory cache) {
        this.cache = cache;
        this.delegate = delegate;
    }

    @Override
    public <T> T query(String statement, Object[] parameter, Class pojo) {
        // 计算CacheKey
        CacheKey cacheKey = new CacheKey();
        cacheKey.update(statement);
        cacheKey.update(joinStr(parameter));

        LRUCache lruCache = new LRUCache(16);

        if (cache.containsKey(cacheKey.getCode())) {
            // 命中缓存
            System.out.println("命中缓存");
            return (T) cache.get(cacheKey.getCode());
        } else {
            System.out.println("去数据库查询数据");
            Object obj = delegate.query(statement, parameter, pojo);
            cache.put(cacheKey.getCode(), obj);
            System.out.println("查询结果放入缓存中");
            return (T) obj;
        }

    }

    /**
     * 拼接入参字符串 逗号分割
     *
     * @param objs
     * @return
     */
    private String joinStr(Object[] objs) {
        StringBuffer sb = new StringBuffer();
        if (objs != null && objs.length > 0) {
            for (Object objStr : objs) {
                sb.append(objStr.toString() + ",");
            }
        }
        int len = sb.length();
        if (len > 0) {
            sb.deleteCharAt(len - 1);
        }
        return sb.toString();
    }
}

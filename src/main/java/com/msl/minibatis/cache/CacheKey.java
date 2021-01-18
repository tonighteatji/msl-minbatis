package com.msl.minibatis.cache;
import lombok.Data;
/**
 * 缓存key
 * @Author msl
 * @Date 2021-01-17 21:23
 */
public class CacheKey {

    /** 在minibatis.properties中读取这个key值来确定是否开启缓存 */
    public static final String CACHE_ENABLED = "cache.enabled";

    /** 默认哈希值 */
    private static final int DEFAULT_HASHCODE = 17;

    /** 倍数 */
    private static final int DEFAULT_MULTIPLIER = 37;

    private int hashCode;
    private int count;
    private int multiplier;

    /**
     * 构造函数
     */
    public CacheKey() {
        this.hashCode = DEFAULT_HASHCODE;
        this.count = 0;
        this.multiplier = DEFAULT_MULTIPLIER;
    }

    /**
     * 返回CacheKey的值
     * @return
     */
    public int getCode() {
        return hashCode;
    }

    /**
     * 计算CacheKey中的HashCode
     * @param object
     */
    public void update(Object object) {
        int baseHashCode = object == null ? 1 : object.hashCode();
        count++;
        baseHashCode *= count;
        hashCode = multiplier * hashCode + baseHashCode;
    }
}

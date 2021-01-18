package com.msl.minibatis.plugin;

/**
 * 拦截器接口，所有自定义拦截器必须实现此接口
 *
 * @Author msl
 * @Date 2021-01-18 21:23
 */
public interface Interceptor {
    /**
     * 插件的核心逻辑实现
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 对被拦截对象进行代理
     *
     * @param target
     * @return
     */
    Object plugin(Object target);
}

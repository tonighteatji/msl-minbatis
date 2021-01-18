package com.msl.minibatis.executor;

/**
 * 执行器
 * @Author msl
 * @Date 2021-01-17 21:13
 */
public interface Executor {
    <T> T query(String statement, Object[] parameter, Class pojo);
}

package com.msl.minibatis.binding;

import com.msl.minibatis.session.DefaultSqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * mapper注册，获取MapperProxy代理对象
 *
 * @Author msl
 * @Date 2021-01-17 20:03
 */
public class MapperRegistry {

    /**
     * 接口和工厂类映射关系
     */
    private final Map<Class<?>, MapperProxyFactory> knownMappers = new HashMap<>();

    /**
     * 在Configuration中解析接口上的注解时，存入接口和工厂类的映射关系
     * 此处传入pojo类型，是为了最终处理结果集的时候将结果转换为POJO类型
     *
     * @param clazz mapper接口类
     * @param pojo
     * @param <T>
     */
    public <T> void addMapper(Class<T> clazz, Class pojo) {
        // 初始化mapper代理工厂类
        MapperProxyFactory mapperProxyFactory = new MapperProxyFactory(clazz, pojo);
        // 放入knownMappers中 享元模式
        knownMappers.put(clazz, mapperProxyFactory);
    }

    /**
     * 创建一个代理对象
     * clazz mapper接口类
     * sqlSession包含了配置类，配置类存放了相应的statementId
     */
    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {

        // 获取该clazz的代理工厂类
        // proxyFactory.mapperInterface 等同于 clazz
        MapperProxyFactory proxyFactory = knownMappers.get(clazz);

        if (proxyFactory == null) {
            throw new RuntimeException("Type: " + clazz + " can not find");
        }
        // 返回mapper代理类
        //
        return (T) proxyFactory.newInstance(sqlSession);
    }
}

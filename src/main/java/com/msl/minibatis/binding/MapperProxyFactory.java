package com.msl.minibatis.binding;

import com.msl.minibatis.session.DefaultSqlSession;

import java.lang.reflect.Proxy;

/**
 * MapperProxy工厂类
 *
 * @Author msl
 * @Date 2021-01-17 20:14
 */
public class MapperProxyFactory<T> {

    /**
     * mapper接口类
     */
    private Class<T> mapperInterface;

    /**
     * pojo对象
     */
    private Class object;

    public MapperProxyFactory(Class<T> mapperInterface, Class object) {
        this.mapperInterface = mapperInterface;
        this.object = object;
    }

    /**
     * 调用Proxy.newProxyInstance方法实现实例化 MapperProxy對象
     * @param sqlSession sqlSession包含了配置类，配置类存放了相应的statementId
     * @return 返回对象强制转换成 mapperInterface mapper接口类
     */
    public T newInstance(DefaultSqlSession sqlSession) {
        // 获取接口类
        Class[] interfaces = new Class[]{mapperInterface};
        // jdk代理对象
        // sqlSession包含了配置类，配置类存放了相应的statementId
        MapperProxy mapperProxy = new MapperProxy(sqlSession, object);
        // jdk动态代理3个参数
        // 1类加载器 2 被代理类实现的接口（这里没有被代理类） 3 InvocationHandler实现类
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), interfaces, mapperProxy);
    }
}

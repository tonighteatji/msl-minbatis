package com.msl.minibatis.binding;

import com.msl.minibatis.session.DefaultSqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * mapper代理类 jdk动态代理
 * @Author msl
 * @Date 2021-01-17 21:22
 */
public class MapperProxy implements InvocationHandler {

    /**
     * sqlSession包含了配置类，配置类存放了相应的statementId
     */
    private DefaultSqlSession sqlSession;

    /**
     * pojo对象
     */
    private Class object;

    public MapperProxy(DefaultSqlSession sqlSession, Class object) {
        this.sqlSession = sqlSession;
        this.object = object;
    }

    /**
     * 所有Mapper接口的方法调用都会走到这里
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取mapper接口类名
       String mapperInterfaceName = method.getDeclaringClass().getName();
       String methodName = method.getName();
       // 根据接口类名和方法名拼接出statementId
       String statementId = mapperInterfaceName + "." + methodName;
        // 如果根据接口类型+方法名能找到映射的SQL，则执行SQL
        if (sqlSession.getConfiguration().hasStatement(statementId)) {
            return sqlSession.selectOne(statementId, args, object);
        }
        // 否则直接执行被代理对象的原方法
        return method.invoke(proxy, args);
    }
}

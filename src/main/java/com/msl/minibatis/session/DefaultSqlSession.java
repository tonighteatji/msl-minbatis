package com.msl.minibatis.session;

import com.msl.minibatis.executor.Executor;

/**
 * sqlSession  api
 * 在实际mybatis中DefaultSqlSession是线程不安全的，底层是HashMap
 * Mybatis-Spring整合后通过SqlSessionTemplate类实现线程安全，该类对DefaultSqlSession进行jdk动态代理,在所有DAO层共享一个实例（默认单例）
 * SqlSessionTemplate是通过代理拦截和SqlSessionHolder实现的sqlsession线程安全和自动新建和释放连接的。
 * 看构造函数函数中构建代理类，该代理类实现SqlSession接口，定义了方法拦截器.
 * 如果调用代理类实例中实现SqlSession接口定义的方法，该调用则被导向SqlSessionInterceptor的invoke方法，这个方法中自动进行了SqlSession的自动请求和释放
 * 如果不被spring托管则自己新建和释放sqlsession，如果被spring管理则使用SqlSessionHolder进行request和relase操作
 * <p>
 * 不使用Spring框架时要获取线程安全的SqlSession,mybatis也实现了SqlSessionManager类，该类实现了SqlSessionFactory
 * SqlSession并且在其中定义了一个TreadLocal的SqlSession对象，同时使用了代理拦截进行了SqlSession的自动管理
 *
 * @Author msl
 * @Date 2021-01-17 20:15
 */
public class DefaultSqlSession {

    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        // 根据全局配置决定是否使用缓存装饰
        this.executor = configuration.newExecutor();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 获取mapper
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> clazz) {
        return configuration.getMapper(clazz, this);
    }

    /**
     * 1.根据statement获取配置类中的具体sql语句
     * 2.调用executor.query方法，executor.query会处理入参，查询语句，并且将ResultSet封装成pojo类返回
     *
     * @param statement
     * @param parameter
     * @param pojo
     * @param <T>
     * @return
     */
    public <T> T selectOne(String statement, Object[] parameter, Class pojo) {
        String sql = getConfiguration().getMappedStatement(statement);
        return executor.query(sql, parameter, pojo);
    }
}

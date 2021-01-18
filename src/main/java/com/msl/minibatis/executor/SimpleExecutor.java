package com.msl.minibatis.executor;

/**
 * @Author msl
 * @Date 2021-01-17 21:21
 */
public class SimpleExecutor implements Executor {

    /**
     * 调用statementHandler去执行query
     *
     * @param statement
     * @param parameter
     * @param pojo
     * @param <T>
     * @return
     */
    @Override
    public <T> T query(String statement, Object[] parameter, Class pojo) {
        StatementHandler statementHandler = new StatementHandler();
        return statementHandler.query(statement, parameter, pojo);
    }
}

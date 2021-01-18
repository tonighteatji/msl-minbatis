package com.msl.minibatis.executor;

import com.msl.minibatis.session.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 操封装了JDBC Statement 作数据库
 *
 * @Author msl
 * @Date 2021-01-18 21:21
 */
public class StatementHandler {

    /**
     * 结果处理封装成pojo类
     */
    private ResultSetHandler resultSetHandler = new ResultSetHandler();

    /**
     * 查询接口
     *
     * @param statement
     * @param parameter
     * @param pojo
     * @param <T>
     * @return
     */
    public <T> T query(String statement, Object[] parameter, Class pojo) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        Object result = null;

        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(statement);
            ParameterHandler parameterHandler = new ParameterHandler(preparedStatement);
            parameterHandler.setParameters(parameter);
            preparedStatement.execute();
            try {
                result = resultSetHandler.handle(preparedStatement.getResultSet(), pojo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return (T)result;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
        // 只在try里面return会报错
        return null;
    }

    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    private Connection getConnection() {
        String driver = Configuration.properties.getString("jdbc.driver");
        String url =  Configuration.properties.getString("jdbc.url");
        String username = Configuration.properties.getString("jdbc.username");
        String password = Configuration.properties.getString("jdbc.password");
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}

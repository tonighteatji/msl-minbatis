package com.msl.minibatis;

import com.msl.minibatis.entity.User;
import com.msl.minibatis.mapper.UserMapper;
import com.msl.minibatis.session.DefaultSqlSession;
import com.msl.minibatis.session.SqlSessionFactory;

/**
 * @Author msl
 * @Date 2021-01-18 23:36
 */
public class TestMiniBatis {
    public static void main(String[] args) {
        SqlSessionFactory factory = new SqlSessionFactory();
        DefaultSqlSession sqlSession = factory.build().openSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectUserById(1);
        System.out.println("第一次查询: " + user);

        System.out.println("第二次查询，测试缓存");
        User user2 = userMapper.selectUserById(1);
        System.out.println("第2次查询: " + user2);

    }
}

package com.msl.minibatis.executor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author msl
 * @Date 2021-01-17 21:21
 */
public class ResultSetHandler {

    /**
     * 返回的数据类型封装成pojo类
     *
     * @param resultSet
     * @param pojo
     * @param <T>
     * @return
     */
    public <T> T handle(ResultSet resultSet, Class pojo) {
        Object o = null;
        try {
            o = pojo.newInstance();
            if (resultSet.next()) {
                // 循环赋值
                for (Field field : o.getClass().getDeclaredFields()) {
                    setValue(o, field, resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) o;
    }


    /**
     * 通过反射给属性赋值
     *
     * @param pojo
     * @param field
     * @param rs
     */
    private void setValue(Object pojo, Field field, ResultSet rs) {
        try {
            // 获取 pojo 的 set 方法
            Method setMethod = pojo.getClass().getMethod("set" + firstWordCapital(field.getName()), field.getType());
            // 调用 pojo 的set 方法，使用结果集给属性赋值
            // 赋值先从resultSet取出值
            setMethod.invoke(pojo, getResult(rs, field));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据反射判断类型，从ResultSet中取对应类型参数
     *
     * @param rs
     * @param field
     * @return
     * @throws SQLException
     */
    private Object getResult(ResultSet rs, Field field) throws SQLException {
        //TODO TypeHandler
        Class type = field.getType();
        // 驼峰转下划线
        String dataName = HumpToUnderline(field.getName());
        // TODO 类型判断不够全
        if (Integer.class == type) {
            return rs.getInt(dataName);
        } else if (String.class == type) {
            return rs.getString(dataName);
        } else if (Long.class == type) {
            return rs.getLong(dataName);
        } else if (Boolean.class == type) {
            return rs.getBoolean(dataName);
        } else if (Double.class == type) {
            return rs.getDouble(dataName);
        } else {
            return rs.getString(dataName);
        }
    }

    /**
     * 数据库下划线转Java驼峰命名
     *
     * @param para
     * @return
     */
    public static String HumpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 单词首字母大写
     *
     * @param word
     * @return
     */
    private String firstWordCapital(String word) {
        String first = word.substring(0, 1);
        String tail = word.substring(1);
        return first.toUpperCase() + tail;
    }
}

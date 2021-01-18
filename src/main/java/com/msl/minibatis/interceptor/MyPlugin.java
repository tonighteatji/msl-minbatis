package com.msl.minibatis.interceptor;

import com.msl.minibatis.annotation.Intercepts;
import com.msl.minibatis.plugin.Interceptor;
import com.msl.minibatis.plugin.Invocation;
import com.msl.minibatis.plugin.Plugin;

import java.util.Arrays;

/**
 * @Author msl
 * @Date 2021-01-17 21:25
 */
@Intercepts("query")
public class MyPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String statement = (String) invocation.getArgs()[0];
        Object[] parameter = (Object[]) invocation.getArgs()[1];
        Class pojo = (Class) invocation.getArgs()[2];
        System.out.println("进入自定义插件：MyPlugin");
        System.out.println("SQL：["+statement+"]");
        System.out.println("Parameters："+ Arrays.toString(parameter));
        // todo 想实现类似于 PageHelper分页功能，看了一下它的源码，好像也不是很难实现，先咕了。
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }
}


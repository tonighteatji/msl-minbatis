package com.msl.minibatis.annotation;

import java.lang.annotation.*;

/**
 * 拦截器，用于插件功能的实现
 *
 * @Author msl
 * @Date 2021-01-17 19:54
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Intercepts {
    String value();
}

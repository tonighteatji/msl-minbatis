package com.msl.minibatis.annotation;

import java.lang.annotation.*;

/**
 * 映射返回的实体类
 * @Author msl
 * @Date 2021-01-17 19:54
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {
    Class<?> value();
}

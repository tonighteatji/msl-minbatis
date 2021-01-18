package com.msl.minibatis.annotation;

        import java.lang.annotation.*;

/**
 * 配置sql查询语句
 * @Author msl
 * @Date 2021-01-17 19:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {
    String value();
}


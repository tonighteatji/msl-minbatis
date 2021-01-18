package com.msl.minibatis.session;

import com.msl.minibatis.TestMiniBatis;
import com.msl.minibatis.annotation.Entity;
import com.msl.minibatis.annotation.Select;
import com.msl.minibatis.binding.MapperRegistry;
import com.msl.minibatis.cache.*;
import com.msl.minibatis.executor.CachingExecutor;
import com.msl.minibatis.executor.Executor;
import com.msl.minibatis.executor.SimpleExecutor;
import com.msl.minibatis.plugin.Interceptor;
import com.msl.minibatis.plugin.InterceptorChain;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 读取全局配置
 *
 * @Author msl
 * @Date 2021-01-17 19:59
 */
@Slf4j
public class Configuration {

    /**
     * sql映射关系配置，使用注解时不用配置
     */
    public static final ResourceBundle sqlMappings;

    /**
     * 全局配置
     */
    public static final ResourceBundle properties;
    /**
     * 维护接口与工厂类关系
     */
    public static final MapperRegistry MAPPER_REGISTRY = new MapperRegistry();
    /**
     * 维护接口方法与SQL关系
     */
    public static final Map<String, String> mappedStatements = new HashMap<>();

    /**
     * 插件
     */
    private InterceptorChain interceptorChain = new InterceptorChain();
    /**
     * 所有Mapper接口
     */
    private List<Class<?>> mapperList = new ArrayList<>();
    /**
     * 类所有文件
     */
    private List<String> classPaths = new ArrayList<>();

    /**
     * 读取resources路径下的properties文件
     */
    static {
        sqlMappings = ResourceBundle.getBundle("sql");
        properties = ResourceBundle.getBundle("minibatis");
    }

    /**
     * 初始化时解析全局配置文件
     * 1.解析sql.properties
     * 2.解析minibatis.properties
     */
    public Configuration() {
        // 1.解析sql.properties 在properties和注解中重复配置SQL会覆盖
        // 获取sql中的键值对
        for (String key : sqlMappings.keySet()) {
            Class mapper = null;
            // sql语句
            String statement = null;
            // pojo对象string值
            String pojoStr = null;
            // pojo对象类
            Class pojo = null;

            // properties中的value用--隔开，第一个是SQL语句
            statement = sqlMappings.getString(key).split("--")[0];
            // properties中的value用--隔开，第二个是需要转换的POJO类型
            pojoStr = sqlMappings.getString(key).split("--")[1];
            try {
                // properties中的key是接口类型+方法
                // 从接口类型+方法中截取接口类型
                mapper = Class.forName(key.substring(0, key.lastIndexOf(".")));
                pojo = Class.forName(pojoStr);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // 接口与返回的实体类关系
            MAPPER_REGISTRY.addMapper(mapper, pojo);
            // 接口方法与SQL关系
            mappedStatements.put(key, statement);
        }
        // 2.解析接口上的注解（会覆盖XML中的接口与实体类的关系）
        String mapperPath = properties.getString("mapper.path");
        scanPackage(mapperPath);
        for (Class<?> mapper : mapperList) {
            parsingClass(mapper);
        }
        // 3.解析插件，可配置多个插件
        String pluginPathValue = properties.getString("plugin.path");
        String[] pluginPaths = pluginPathValue.split(",");
        if (pluginPaths != null) {
            // 将插件添加到interceptorChain中
            for (String plugin : pluginPaths) {
                Interceptor interceptor = null;
                try {
                    interceptor = (Interceptor) Class.forName(plugin).newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                interceptorChain.addInterceptor(interceptor);
            }
        }
    }

    /**
     * 根据statement判断是否存在映射的SQL
     *
     * @param statementName
     * @return
     */
    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    /**
     * 根据statement ID获取SQL
     *
     * @param id
     * @return
     */
    public String getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
        return MAPPER_REGISTRY.getMapper(clazz, sqlSession);
    }

    /**
     * 创建执行器，当开启缓存时使用缓存装饰
     * 当配置插件时，使用插件代理
     *
     * @return
     */
    public Executor newExecutor() {
        Executor executor = null;
        // 读取minibatis.properties中的cache.enabled值
        // Boolean.valueOf() 空值返回false 即默认不开启缓存
        if (Boolean.valueOf(properties.getString(CacheKey.CACHE_ENABLED))) {
            System.out.println("选择器开启缓存");
            String cacheType = properties.getString("cache.type");
            Integer capacity = Integer.valueOf(properties.getString("cache.capacity"));
            CacheFactory cacheFactory;
            if (cacheType == null) {
                System.out.println("默认缓存,用HashMap实现，人有多大胆，缓存多大产");
                cacheFactory = new SimpleCache();
            } else if (cacheType.equals("LRU")) {
                System.out.println("使用LRU缓存,capacity=" + capacity);
                cacheFactory = new LRUCache(capacity);
            } else {
                System.out.println("使用LFU缓存,capacity=" + capacity);
                cacheFactory = new LFUCache(capacity);
            }

            executor = new CachingExecutor(new SimpleExecutor(), cacheFactory);
        } else {
            System.out.println("执行器不开启缓存");
            executor = new SimpleExecutor();
        }

        // 目前只拦截了Executor，所有的插件都对Executor进行代理，没有对拦截类和方法签名进行判断
        if (interceptorChain.hasPlugin()) {
            return (Executor) interceptorChain.pluginAll(executor);
        }
        return executor;
    }

    /**
     * 解析Mapper接口上配置的注解（SQL语句）
     */
    private void parsingClass(Class<?> mapper) {
        // 1.解析类上的注解
        // 如果有Entity注解，说明是查询数据库的接口
        if (mapper.isAnnotationPresent(Entity.class)) {
            for (Annotation annotation : mapper.getAnnotations()) {
                if (annotation.annotationType().equals(Entity.class)) {
                    // 注册接口与实体类的映射关系
                    MAPPER_REGISTRY.addMapper(mapper, ((Entity) annotation).value());
                }
            }
        }

        // 2.解析方法上的注解
        Method[] methods = mapper.getMethods();
        for (Method method : methods) {
            //TODO 其他操作
            // 解析@Select注解的SQL语句
            if (method.isAnnotationPresent(Select.class)) {
                for (Annotation annotation : method.getDeclaredAnnotations()) {
                    if (annotation.annotationType().equals(Select.class)) {
                        // 注册接口类型+方法名和SQL语句的映射关系
                        String statement = method.getDeclaringClass().getName() + "." + method.getName();
                        mappedStatements.put(statement, ((Select) annotation).value());
                    }
                }
            }
        }
    }

    /**
     * 根据全局配置文件的Mapper接口路径，扫描所有接口
     */
    private void scanPackage(String mapperPath) {
        String classPath = TestMiniBatis.class.getResource("/").getPath();
        mapperPath = mapperPath.replace(".", File.separator);
        String mainPath = classPath + mapperPath;
        doPath(new File(mainPath));
        for (String className : classPaths) {
            className = className.replace(classPath.replace("/", "\\").replaceFirst("\\\\", ""), "").replace("\\", ".").replace(".class", "");
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (clazz.isInterface()) {
                mapperList.add(clazz);
            }

        }
    }

    /**
     * 获取文件或文件夹下所有的类.class
     */
    private void doPath(File file) {
        // 文件夹，遍历
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f1 : files) {
                // 递归子文件夹
                doPath(f1);
            }
        } else {
            // 文件，直接添加
            if (file.getName().endsWith(".class")) {
                classPaths.add(file.getPath());
            }
        }
    }
}

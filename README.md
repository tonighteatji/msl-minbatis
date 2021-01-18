# msl-minbatis
手写实现mybatis基础功能
代码在master分支
实现注解Entity用于映射实体类,Intercepts用于拦截器，插件拓展,Select配置SQL语句。
实体类驼峰命名的字段自动转换为数据库字段时变成下划线分割字段。
实现mybatis缓存功能，缓存有3种机制，默认无限缓存由HashMap实现，LRU缓存（淘汰最近最少使用缓存），LFU缓存（淘汰使用频率最少缓存）。
实现mybatis插件拓展功能。

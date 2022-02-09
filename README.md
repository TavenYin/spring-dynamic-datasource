# spring-dynamic-datasource

## 项目介绍
spring boot +mybatis 动态切换、添加数据源demo

## 常见问题

1. 无法切换数据
```java
@Transactional
public User getByTransactional(String targetSource) {
    return userMapper.selectByPrimaryKey(1);
}
```
如果切面拦截的方法，包含了事务，默认情况下，切换数据源是不会生效的。

原理和解决方法参考如下
```java
/**
 * 有事务的情况下，根据参数切换数据源
 *
 * @param ds
 * @return
 */
@GetMapping("/getByTransactional")
public Object getByTransactional(@RequestParam(defaultValue = "dynamic_db0") String ds) {
    try {
        // 默认情况下，事务AOP是要先于 DynamicDataSourceAspect
        // 事务AOP 开启事务，需要向数据库发送 START TRANSACTION，在这之前肯定要通过 DataSource#getConnection 获取连接
        // 在获取完连接之后，会将其存储到上下文中
        // 执行完上述步骤之后，再通过 DynamicDataSourceAspect 指定数据源就没有用了，因为当前线程不会再去调用 DataSource#getConnection 了

        // 所以解决这个问题的原理就清晰了，只需要在开启事务之前指定数据源即可
        // 方法1：手动指定
        // 方法2：改变切面的执行顺序
        DynamicDataSourceContextHolder.setDataSourceKey(ds);
        return userService.getByTransactional(ds);
    } finally {
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }
}
```

## 最后
关于这个项目了解更多：https://www.jianshu.com/p/0a485c965b8b

欢迎关注我的简书或者公众号与我交流

简书：https://www.jianshu.com/u/cd682de00804

公众号：【殷天文】 

![](http://mtw.so/5An2Ue)
# spring-dynamic-datasource

#### 项目介绍
spring boot +mybatis 动态切换、添加数据源demo

#### 软件架构
- AbstractRoutingDataSource 作为 DataSource 路由
- AOP 切面拦截 service 参数，动态切换、创建数据源

我的博客：https://www.jianshu.com/p/0a485c965b8b
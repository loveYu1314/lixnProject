# Spring Boot控制并发登陆人数

​	通常系统都会限制同一个账号的登陆人数，多人登录要么限制后者登录，要么踢出前者，Spring Security提供了这样的功能，本模块没有使用Security，手动实现这个功能。

## 技术选型

1. SpringBoot
2. JWT
3. Filter
4. Redis + Redisson

JWT（token）存储在Redis中，类似JSessionId-Session的关系，用户登陆后每次请求在Header中携带jwt

## 两种思路

### 比较时间戳`CompareKickOutFilter`

维护一个username：jwtToken这样的一个key-value在Redis中，Filter逻辑如下;

1. 从请求的Token中解析username
2. username作为key，在redis中查询对应value，我们叫他RedisToken
3. 比较请求中的token和RedisToken

### 队列踢出`QueueKickOutFilter`

1. 从请求的token中解析username
2. username作为key，在redis中查询对应的value，一个双向队列

## 比较两种方法

第一种逻辑简单粗暴，只维护key-value，不需要使用锁，没有第二种灵活。

第二种代码优雅灵活，但是逻辑复杂一点，而且为了保证线程安全地操作队列，要使用分布式锁

## 演示

运行项目，访问localhost:8887 demo中没有存储用户信息，随意输入用户名密码，用户名相同则被踢出

访问 localhost:8887/index.html 弹出用户信息, 代表当前用户有效

另一个浏览器登录相同用户名，回到第一个浏览器刷新页面，提示被踢出

application.properties中选择开启哪种过滤器模式，默认是比较时间戳踢出，开启队列踢出 queue-filter.enabled=true
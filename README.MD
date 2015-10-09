j360-cloud-all
==============

基于spring cloud应用在分布式系统中的运用

##配置文件上下文##
- bootstrap.yml
- application.yml
- 【注】建议采用bootstrap+application组合形式，一般bootstrap配置工程不可变参数，且在启动时需要设定的内容，application配置可变参数


##spring-cloud-configserver##
===================
- configserver提供了HTTP，为外部资源提供API配置的服务，只需要在Springboot程序中提供@EnableConfigServer注解即可。
    - @EnableConfigServer
    - 增加以下依赖 eureka（可选，作为discover服务器必选）
```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
                    <groupId>org.springframework.cloud</groupId>
                    <artifactId>spring-cloud-starter-eureka</artifactId>
                </dependency>
```
- 运行configserver，通过端点可以查看configserver的属性内容
    - http://localhost:8888/configserver/application.properties

- 【注】本地文件系统仅仅用于测试环境，在生产环境请使用远程的git服务提供配置资源
##Environment Repository 环境变量仓库##
===================
- https://github.com/xuminwlt/config-repo

###环境变量仓库使用3个变量:###
- 查看/env可以看到下面三个属性
    - {application} 在客户端映射到"spring.application.name"
    - {profile} 在客户端映射到"spring.active.profiles"，使用逗号分隔
    - {label}这是一个服务器端功能标签“版本”的一组配置文件


- 默认的环境变量库使用git服务，也可以使用文件系统的服务
    -  Config Server支持一个或者多个git仓库
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          repos:
            simple: https://github.com/simple/config-repo
            special:
              pattern: pattern*,*pattern1*
              uri: https://github.com/special/config-repo
            local:
              pattern: local*
              uri: file:/home/configsvc/config-repo
```

       - Every repository can also optionally store config files in sub-directories, and patterns to search for those directories can be specified as searchPaths. 

```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          searchPaths: foo,bar*
```

        - To use HTTP basic authentication on the remote repository add the "username" and "password" properties separately (not in the URL), e.g

```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/spring-cloud-samples/config-repo
          username: trolley
          password: strongpassword
```

- 文件系统后端
    - 使用 spring.profiles.active=native

- 嵌入式ConfigServer
    - 官方建议独立部署

##spring-cloud-client##
- 使用以下依赖

```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
```

- 配置优先启动
    - 默认的springcloud应用程序启动行为，所有的客户端应用程序想要使用配置服务器的配置，需要：
        -   bootstrap.yml(或一个环境变量)的服务器地址
            - spring.cloud.config.uri(默认为 “http://localhost:8888”)。
    
- Eureka优先启动
    - 如果使用的是Spring CloudNetflix 和eureka service discovery，可以使用配置服务器注册Eureka
    
- 环境变量刷新 
    - post -> localhost:8080/refresh
    - 定时
    - 手动
    
```
The Config Server itself is stateless, so you can spin up as many as these as you need and find them via eureka. Underneath the server itself, the git implementation you point to needs to be highly available as well. So if you point to github (private or public), then git is as available as github is. If the config server can't reach git it will continue to serve what it has checked out even if it is stale.
As far as gradual config changes, you could use a different branch and configure the canary to use that branch via spring.cloud.config.label and them merge the branch. You could also use profiles (eg application-<profilename>.properties) and configure the canary to use the specified profile.
I think the branch makes a little more sense, because you wouldn't have to reconfigure the non-canary nodes to use the new profile each time, just configure canary to use the branch.
Either way, the only time apps see config chages (when using spring cloud config client) is on startup or when you POST to /refresh on each node. You can also POST to /bus/refresh?destination=<servicename> if you use the Spring Cloud Bus to refresh all instances of a service at once.
```
    
- 基于spring cloud应用在分布式系统中的运用
    - config
    - eureka
    - monitor
    - servicediscover

##Spring Cloud Netflix##
-  应用程序+Netflix 组件构建大型分布式系统。包括
    -   服务发现(Eureka)
    -   断路器(Hystrix) 
    -   智能路由(Zuul)
    -   客户端负载均衡(Ribbon)

###服务发现(Eureka)###
- Eureka Client
-   注册
    - @EnableEurekaClient
    - application.yml
    
    ```
        eureka:
          client:
            serviceUrl:
              defaultZone: http://localhost:8761/eureka/
    ```
    
- Eureka Server
-   @EnableEurekaServer
- 【注】服务器有一个主页界面，HTTP API Eureka功能端点 /eureka/
####Standalone Mode####
-   application.yml (Standalone Eureka Server)
    -   serviceUrl指向同一个本地实例的host
    
```
server:
  port: 8761
eureka:
  instance:
    hostname: localhost
  client:
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

####High Availability, Zones and Regions####
- ...

###断路器(Hystrix) ###
- Hystrix Client
- Hystrix DashBoard

###智能路由(Zuul) ###
- Netflix uses Zuul for the following:
    - Authentication
    - Insights
    - Stress Testing
    - Canary Testing
    - Dynamic Routing
    - Service Migration
    - Load Shedding
    - Security
    - Static Response handling
    - Active/Active traffic management


###客户端负载均衡(Ribbon) ###
- Ribbon提供客户端的负载均衡，可以为http和tcp客户端提供控制功能，Feign 同样使用Ribbon
- 定制Ribbon客户端
- Eureka中使用Ribbon

```

```
- 直接使用RibbonAPI

```
public class MyClass {
    @Autowired
    private LoadBalancerClient loadBalancer;
    public void doStuff() {
        ServiceInstance instance = loadBalancer.choose("stores");
        URI storesUri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
        // ... do something with the URI
    }
}
```

##声明式 REST Client: Feign##
- Spring Cloud整合Ribbon和Eureka提供负载均衡的http client时使用Feign.

- 实例案例

```
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableEurekaClient
@EnableFeignClients
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
- 调用(StoreClient.java)

```
@FeignClient("stores")
public interface StoreClient {
    @RequestMapping(method = RequestMethod.GET, value = "/stores")
    List<Store> getStores();
    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathParameter("storeId") Long storeId, Store store);
}
```

###继承支持###

```
UserService.java
public interface UserService {
    @RequestMapping(method = RequestMethod.GET, value ="/users/{id}")
    User getUser(@PathVariable("id") long id);
}
UserResource.java
@RestController
public class UserResource implements UserService {
}
UserClient.java
@FeignClient("users")
public interface UserClient extends UserService {
}
```
##外部配置: Archaius##
- netfilx客户端配置框架
    - push changes到客户端
    - poll 资源来更新资源
- 通常不直接使用

##Spring Cloud Bus##
- Spring Cloud Bus通过轻量级消息代理连接分布式系统的每个节点
- 添加依赖spring-cloud-starter-bus-amqp
- Rabbit config

```
spring:
  rabbitmq:
    host: mybroker.com
    port: 5672
    username: user
    password: secret
```

- http端点
    - /bus/*
    - /bus/env 发送key/values键值对更新spring环境变量
    - /bus/refresh 更新所有的环境变量
    
j360-cloud-all
==============

基于spring cloud应用在分布式系统中的运用

##spring-cloud-configserver##
===================
- @EnableConfigServer

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

- http://localhost:8888/configserver/application.properties
- 可以查看相关的配置信息

##spring-cloud-client##
- 主要依赖
```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

- http://localhost:8080/env

- This is the default behaviour for any application which has the Spring Cloud Config Client on the classpath. When a config client starts up it binds to the Config Server (via the bootstrap configuration property spring.cloud.config.uri) and initializes Spring Environment with remote property sources.
  
- The net result of this is that all client apps that want to consume the Config Server need a bootstrap.yml (or an environment variable) with the server address in spring.cloud.config.uri

###Eureka First Bootstrap##






##Environment Repository 环境变量仓库##

-  https://github.com/xuminwlt/config-repo

- 环境资源配置信息
###The Environment resources are parametrized by three variables:###
- {application} maps to "spring.application.name" on the client side;
- {profile} maps to "spring.active.profiles" on the client (comma separated list); and
- {label}
###Spring Cloud Config Server supports a single or multiple git repositories:###
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
- 检查环境变量仓库是否有效
    - You can configure the Health Indicator to check more applications along with custom profiles and custom labels, e.g.
    - You can disable the Health Indicator by setting spring.cloud.config.server.health.enabled=false.
    ```
      spring:
        cloud:
          config:
            server:
              health:
                repositories:
                  myservice:
                    label: mylabel
                  myservice-dev:
                    name: myservice
                    profiles: development  
    ```
- 嵌入式ConfigServer
    - 嵌入式部署使用配合注解方式
    - 官方建议独立部署
    - The Config Server runs best as a standalone application, but if you need to you can embed it in another application. Just use the @EnableConfigServer annotation and (optionally) set spring.cloud.config.server.prefix to a path prefix, e.g. "/config", to serve the resources under a prefix. The prefix should start but not end with a "/". It is applied to the @RequestMappings in the Config Server (i.e. underneath the Spring Boot prefixes server.servletPath and server.contextPath). Another optional property that can be useful in this case is spring.cloud.config.server.bootstrap which is a flag to indicate that the server should configure itself from its own remote repository. The flag is off by default because it can delay startup, but when embedded in another application it makes sense to initialize the same way as any other application.
    



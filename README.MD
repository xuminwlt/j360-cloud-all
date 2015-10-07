j360-cloud-all
==============

基于spring cloud应用在分布式系统中的运用

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
- {application} 在客户端映射到"spring.application.name"
- {profile} 在客户端映射到"spring.active.profiles"，使用逗号分隔
- {label}这是一个服务器端功能标签“版本”的一组配置文件


- 默认的环境变量库使用git服务，也可以使用文件系统的服务
    -  Spring Cloud Config Server supports a single or multiple git repositories:###
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
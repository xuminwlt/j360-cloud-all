j360-cloud
==============

基于spring cloud应用在分布式系统中的运用

##spring-cloud-configserver##
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

##spring-cloud-client##
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


##Environment Repository 环境变量仓库##
- https://github.com/xuminwlt/config-repo
The Environment resources are parametrized by three variables:
- {application} maps to "spring.application.name" on the client side;
- {profile} maps to "spring.active.profiles" on the client (comma separated list); and
- {label}
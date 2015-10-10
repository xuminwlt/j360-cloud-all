package me.j360.cloud.zookeeperclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with j360-cloud-all -> me.j360.cloud.zookeeperclient.
 * User: min_xu
 * Date: 2015/10/9
 * Time: 21:54
 * 说明：zookeeper还未正式发布 bug较多，作为discovery service服务时，使用@EnableDiscoveryClient
 * zookeeper仅仅是作为数据存储的地方，同eureka一样，但是eureka简单很多，目前推荐eureka
 */
@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
@EnableFeignClients
public class ZookeeperApplication {

    @Value("${spring.application.name:testZookeeperApp}")
    private String appName;

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private Environment env;

    @Autowired
    private AppClient appClient;

    @RequestMapping("/")
    public ServiceInstance lb() {
        return loadBalancer.choose(appName);
    }

    @RequestMapping("/hi")
    public String hi() {
        return "Hello World!";
    }

    @RequestMapping("/self")
    public String self() {
        return appClient.hi();
    }

    @RequestMapping("/myenv")
    public String env(@RequestParam("prop") String prop) {
        String property = new RelaxedPropertyResolver(env).getProperty(prop, "Not Found");
        return property;
    }

    @FeignClient("testZookeeperApp")
    interface AppClient {
        @RequestMapping(value = "/hi", method = RequestMethod.GET)
        String hi();
    }

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplication.class, args);
    }
}

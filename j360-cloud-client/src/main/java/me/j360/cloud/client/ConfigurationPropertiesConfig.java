package me.j360.cloud.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * Created with springbootweb -> me.j360.springboot.simple.bean.config.
 * User: min_xu
 * Date: 2015/7/29
 * Time: 13:50
 * 说明：另一种配置方法
 */

@RefreshScope
@Component
@Configuration
@ConfigurationProperties(prefix="user")
public class ConfigurationPropertiesConfig {
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    private String username;
}

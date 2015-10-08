package me.j360.cloud.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with j360-cloud-all -> me.j360.cloud.client.
 * User: min_xu
 * Date: 2015/10/7
 * Time: 22:37
 * 说明：
 */

@RestController
@RefreshScope
public class MessageController {

    @Value("${apply.message}")
    public String message;

    @Autowired
    private ConfigurationPropertiesConfig configurationPropertiesConfig;


    @RequestMapping("/")
    public String home() {
        return message;
    }

    @RequestMapping("/user")
    public String user() {
        return configurationPropertiesConfig.getUsername();
    }
}

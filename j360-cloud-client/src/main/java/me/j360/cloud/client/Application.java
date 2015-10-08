package me.j360.cloud.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;

/**
 * Created with j360-cloud-all -> me.j360.cloud.client.
 * User: min_xu
 * Date: 2015/9/30
 * Time: 11:03
 * 说明：
 */

@SpringBootApplication
@EnableScheduling
//@EnableEurekaClient
public class Application {

    @Autowired
    private org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope;


    @Autowired
    private ConfigurationPropertiesConfig configurationPropertiesConfig;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Scheduled(cron="0/20 * *  * * ? ")   //每20秒执行一次
    public void refreshConfigProperties(){
        refresh();
        System.out.println(configurationPropertiesConfig.getUsername());
    }

    private Set<String> standardSources = new HashSet<String>(Arrays.asList(
            StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
            StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
            StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME,
            StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
            StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME));

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private org.springframework.cloud.context.scope.refresh.RefreshScope scope;

    /**
     * 手动刷新git更新方案，每20S执行一次，可以修改成手动执行，同/refresh
     * @link RefreshEndpoint
     * */
    public void refresh() {
        Map<String, Object> before = extract(context.getEnvironment()
                .getPropertySources());
        addConfigFilesToEnvironment();
        Set<String> keys = changes(before,
                extract(context.getEnvironment().getPropertySources())).keySet();
        scope.refreshAll();

        context.publishEvent(new EnvironmentChangeEvent(keys));
    }
    @Configuration
    protected static class Empty {

    }

    private void addConfigFilesToEnvironment() {
        ConfigurableApplicationContext capture = null;
        try {
            capture = new SpringApplicationBuilder(Empty.class).showBanner(false)
                    .web(false).environment(context.getEnvironment()).run();
            MutablePropertySources target = context.getEnvironment().getPropertySources();
            for (PropertySource<?> source : capture.getEnvironment().getPropertySources()) {
                String name = source.getName();
                if (!standardSources.contains(name)) {
                    if (target.contains(name)) {
                        target.replace(name, source);
                    }
                    else {
                        if (target.contains("defaultProperties")) {
                            target.addBefore("defaultProperties", source);
                        }
                        else {
                            target.addLast(source);
                        }
                    }
                }
            }
        }
        finally {
            while (capture != null) {
                capture.close();
                ApplicationContext parent = capture.getParent();
                if (parent instanceof ConfigurableApplicationContext) {
                    capture = (ConfigurableApplicationContext) parent;
                } else {
                    capture = null;
                }
            }
        }
    }

    private Map<String, Object> changes(Map<String, Object> before,
                                        Map<String, Object> after) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                result.put(key, null);
            }
            else if (!equal(before.get(key), after.get(key))) {
                result.put(key, after.get(key));
            }
        }
        for (String key : after.keySet()) {
            if (!before.containsKey(key)) {
                result.put(key, after.get(key));
            }
        }
        return result;
    }

    private boolean equal(Object one, Object two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    private Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (PropertySource<?> parent : propertySources) {
            if (!standardSources.contains(parent.getName())) {
                extract(parent, result);
            }
        }
        return result;
    }

    private void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource) {
            try {
                for (PropertySource<?> source : ((CompositePropertySource) parent)
                        .getPropertySources()) {
                    extract(source, result);
                }
            }
            catch (Exception e) {
                return;
            }
        }
        else if (parent instanceof EnumerablePropertySource) {
            for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
                result.put(key, parent.getProperty(key));
            }
        }
    }

}

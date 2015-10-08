package me.j360.cloud.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Scheduled(cron="0/5 * *  * * ? ")   //每5秒执行一次
    public void refreshConfigProperties(){
        //System.out.println("start");
        refreshScope.refreshAll();
    }
}

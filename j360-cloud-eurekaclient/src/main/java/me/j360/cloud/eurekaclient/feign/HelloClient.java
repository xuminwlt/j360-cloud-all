package me.j360.cloud.eurekaclient.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created with j360-cloud-all -> me.j360.cloud.eurekaclient.feign.
 * User: min_xu
 * Date: 2015/10/9
 * Time: 10:49
 * 说明：映射到service中的hello rest,在controller中直接调用helloClient即可
 */

@FeignClient("eurekaservice")
public interface HelloClient {
    @RequestMapping(value = "/", method = GET)
    String hello();
}
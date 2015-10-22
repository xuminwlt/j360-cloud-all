package me.j360.cloud.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.cluster.lock.DistributedLock;
import org.springframework.cloud.cluster.redis.lock.RedisLockService;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
	public RedisTemplate redisTemplate(){
		RedisTemplate redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	@Bean
	public RedisLockService redisLockService(){
		RedisLockService lockService = new RedisLockService(redisConnectionFactory);
		return lockService;
	}

	@Scheduled(cron="0/20 * *  * * ? ")   //每20秒执行一次
	public void refreshConfigProperties(){
		//分布式锁实现
		cleanLocks();
		DistributedLock lock = redisLockService().obtain("lock");
		System.out.println(lock.getLockKey());
		lock.lock();
		Set<String> keys = redisTemplate().keys(RedisLockService.DEFAULT_REGISTRY_KEY + ":*");
		System.out.println(keys.size()); //1
		System.out.println(keys.iterator().next()); //RedisLockService.DEFAULT_REGISTRY_KEY + ":lock"
		lock.unlock();
		cleanLocks();

		//Zookeeper实现

	}

	private void cleanLocks() {
		Set<String> keys = redisTemplate().keys(RedisLockService.DEFAULT_REGISTRY_KEY + ":*");
		redisTemplate().delete(keys);
	}
}

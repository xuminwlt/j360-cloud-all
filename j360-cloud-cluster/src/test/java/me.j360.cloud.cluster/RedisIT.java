package me.j360.cloud.cluster;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.EnvironmentTestUtils;
import org.springframework.cloud.cluster.lock.DistributedLock;
import org.springframework.cloud.cluster.redis.lock.RedisLockService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with j360-cloud-all -> me.j360.cloud.cluster.
 * User: min_xu
 * Date: 2015/10/12
 * Time: 11:19
 * 说明：分布式锁
 */
public class RedisIT {

    private AnnotationConfigApplicationContext context;
    private RedisConnectionFactory connectionFactory;
    private RedisTemplate<String, String> redisTemplate;

    @Before
    public void setup() {
        context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context);
        context.register(RedisAutoConfiguration.class);
        context.refresh();
        connectionFactory = context.getBean(RedisConnectionFactory.class);
        redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        cleanLocks();
    }

    @After
    public void close() {
        cleanLocks();
        context.close();
    }

    private void cleanLocks() {
        Set<String> keys = redisTemplate.keys(RedisLockService.DEFAULT_REGISTRY_KEY + ":*");
        redisTemplate.delete(keys);
    }

    @Test
    public void testSimpleLock() {
        RedisLockService lockService = new RedisLockService(connectionFactory);
        DistributedLock lock = lockService.obtain("lock");
        lock.lock();

        Set<String> keys = redisTemplate.keys(RedisLockService.DEFAULT_REGISTRY_KEY + ":*");
        assertThat(keys.size(), is(1));
        assertThat(keys.iterator().next(), is(RedisLockService.DEFAULT_REGISTRY_KEY + ":lock"));

        lock.unlock();
    }

    @Test
    public void testSecondLockSucceed() {
        RedisLockService lockService = new RedisLockService(connectionFactory);
        DistributedLock lock1 = lockService.obtain("lock");
        DistributedLock lock2 = lockService.obtain("lock");
        lock1.lock();
        // same thread so try/lock doesn't fail
        assertThat(lock2.tryLock(), is(true));
        lock2.lock();

        Set<String> keys = redisTemplate.keys(RedisLockService.DEFAULT_REGISTRY_KEY + ":*");
        assertThat(keys.size(), is(1));
        assertThat(keys.iterator().next(), is(RedisLockService.DEFAULT_REGISTRY_KEY + ":lock"));

        lock1.unlock();
        lock2.unlock();
    }

}
package cn.chinogo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

/**
 * Redis自动配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(JedisPool.class)
@ConditionalOnProperty(prefix = "redis", havingValue = "false", value = "cluster", matchIfMissing = false)
public class RedisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisAutoConfiguration.class);

    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(properties.getSingleHost(), properties.getSinglePort());
    }


}

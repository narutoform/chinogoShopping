package cn.chinogo.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;

/**
 * Redis自动配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(JedisPool.class)
@ConditionalOnProperty(prefix = "redis", havingValue = "true", value = "cluster", matchIfMissing = false)
public class RedisClusterAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(RedisClusterAutoConfiguration.class);

    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisCluster jedisCluster() {

        String clusterNodes = properties.getClusterNodes();

        if (StringUtils.isEmpty(clusterNodes)) {
            logger.error("clusterNodes不能为空!");
            throw new RuntimeException();
        }

        String[] nodes = clusterNodes.split(",");

        HashSet<HostAndPort> hostAndPorts = new HashSet<>();

        for (String node : nodes) {
            String[] split = node.split(":");
            HostAndPort hostAndPort = new HostAndPort(split[0], Integer.parseInt(split[1]));
            hostAndPorts.add(hostAndPort);
        }

        JedisCluster jedisCluster = new JedisCluster(hostAndPorts);

        return jedisCluster;
    }


}

package cn.chinogo.redis.service.impl;

import cn.chinogo.constant.Const;
import cn.chinogo.redis.service.JedisClient;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service(version = Const.CHINOGO_REDIS_VERSION, timeout = 1000000)
public class JedisClientSingle implements JedisClient {

    @Autowired
    private JedisPool jedisPool;

    @Value("${redis.password}")
    private String password;

    private Jedis getResource() {
        Jedis resource = jedisPool.getResource();
        if (StringUtils.isBlank(password)) {
            return resource;
        } else {
            resource.auth(password);
            return resource;
        }
    }

    @Override
    public String get(String key) {
        Jedis resource = null;
        String string = null;

        try {
            resource = getResource();
            string = resource.get(key);
        } finally {
            resource.close();
        }

        return string;

    }

    @Override
    public String set(String key, String value) {
        Jedis resource = null;
        String string = null;

        try {
            resource = getResource();
            string = resource.set(key, value);
        } finally {
            resource.close();
        }

        return string;

    }

    @Override
    public String hget(String hkey, String key) {
        Jedis resource = null;
        String string = null;
        try {
            resource = getResource();
            string = resource.hget(hkey, key);
        } finally {
            resource.close();
        }

        return string;

    }

    @Override
    public long hset(String hkey, String key, String value) {
        Jedis resource = null;
        Long hset = null;

        try {
            resource = getResource();
            hset = resource.hset(hkey, key, value);
        } finally {
            resource.close();
        }

        return hset;

    }

    @Override
    public long incr(String key) {
        Jedis resource = null;
        Long incr = null;

        try {
            resource = getResource();
            incr = resource.incr(key);
        } finally {
            resource.close();
        }

        return incr;

    }

    @Override
    public long expire(String key, Integer second) {
        Jedis resource = null;
        Long expire = null;

        try {
            resource = getResource();
            expire = resource.expire(key, second);
        } finally {
            resource.close();
        }

        return expire;

    }

    @Override
    public long ttl(String key) {
        Jedis resource = null;
        Long ttl = null;

        try {
            resource = getResource();
            ttl = resource.ttl(key);
        } finally {
            resource.close();
        }

        return ttl;
    }

    @Override
    public long del(String key) {
        Jedis resource = null;
        Long del = null;

        try {
            resource = getResource();
            del = resource.del(key);
        } finally {
            resource.close();
        }

        return del;
    }

    @Override
    public long hdel(String hkey, String key) {
        Jedis resource = null;
        Long hdel = null;

        try {
            resource = getResource();
            hdel = resource.hdel(hkey, key);
        } finally {
            resource.close();
        }

        return hdel;
    }
}

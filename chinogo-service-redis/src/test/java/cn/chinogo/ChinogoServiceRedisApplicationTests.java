package cn.chinogo;

import cn.chinogo.redis.service.JedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChinogoServiceRedisApplicationTests {

	@Autowired
	private JedisClient jedisClient;

	@Test
	public void contextLoads() {

	}

}

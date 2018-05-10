package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class ChinogoServiceRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinogoServiceRedisApplication.class, args);

		Main.main(args);
	}
}

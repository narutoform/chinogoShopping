package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author chinotan
 */
@Configuration
@SpringBootApplication
public class ChinogoWebOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinogoWebOrderApplication.class, args);
		Main.main(new String[0]);
	}
}

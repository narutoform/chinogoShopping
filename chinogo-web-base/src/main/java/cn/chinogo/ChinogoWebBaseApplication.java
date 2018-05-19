package cn.chinogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author chinotan
 */
@Configuration
@SpringBootApplication
public class ChinogoWebBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinogoWebBaseApplication.class, args);
	}
}

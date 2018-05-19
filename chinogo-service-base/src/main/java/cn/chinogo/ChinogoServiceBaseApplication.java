package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableTransactionManagement
public class ChinogoServiceBaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinogoServiceBaseApplication.class, args);
		Main.main(new String[0]);
	}
}

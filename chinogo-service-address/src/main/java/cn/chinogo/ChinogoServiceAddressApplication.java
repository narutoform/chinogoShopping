package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableTransactionManagement
public class ChinogoServiceAddressApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChinogoServiceAddressApplication.class, args);
		Main.main(args);
	}
}

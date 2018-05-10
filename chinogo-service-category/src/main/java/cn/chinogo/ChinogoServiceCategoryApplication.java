package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("cn.chinogo.mapper")
public class ChinogoServiceCategoryApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChinogoServiceCategoryApplication.class, args);
		Main.main(args);
	}
}

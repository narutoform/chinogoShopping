package cn.chinogo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = "cn.chinogo.mapper")
public class ChinogoServiceContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinogoServiceContentApplication.class, args);

    }
}

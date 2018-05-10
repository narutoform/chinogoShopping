package cn.chinogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SpringBootApplication
@EnableTransactionManagement
public class ChinogoServiceCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChinogoServiceCartApplication.class, args);

    }
}

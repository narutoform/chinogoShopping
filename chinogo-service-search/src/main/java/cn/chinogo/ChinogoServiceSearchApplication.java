package cn.chinogo;

import com.alibaba.dubbo.container.Main;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@SpringBootApplication
public class ChinogoServiceSearchApplication {
    
    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication.run(ChinogoServiceSearchApplication.class, args);
        Main.main(new String[0]);
    }

    @Bean
    public TransportClient transportClient() {

        InetSocketTransportAddress address = null;
        try {
            address = new InetSocketTransportAddress(InetAddress.getByName(host), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        TransportClient transportClient = new PreBuiltTransportClient(Settings.EMPTY);

        transportClient.addTransportAddress(address);

        return transportClient;
    }
}

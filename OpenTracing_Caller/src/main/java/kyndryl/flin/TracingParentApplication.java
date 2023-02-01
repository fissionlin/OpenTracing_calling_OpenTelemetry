package kyndryl.flin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Fission Lin
 * @Date: 2023/1/30
 * @Time: 4:51 PM
 * @Description: kyndryl.flin
 */
@SpringBootApplication
public class TracingParentApplication {
    public static void main(String[] args) {
        SpringApplication.run(TracingParentApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

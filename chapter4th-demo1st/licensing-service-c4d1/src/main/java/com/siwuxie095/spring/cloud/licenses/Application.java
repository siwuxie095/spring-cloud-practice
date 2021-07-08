package com.siwuxie095.spring.cloud.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:34:38
 */
@SuppressWarnings("all")
@SpringBootApplication
// 激活 Spring DiscoveryClient
@EnableDiscoveryClient
// 需要使用 @EnableFeignClients 以在代码中启用 Feign 客户端
@EnableFeignClients
public class Application {

    // @LoadBalanced 注解告诉 Spring Cloud 创建一个支持 Ribbon 的 RestTemplate 类
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

package com.siwuxie095.spring.cloud.confsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 1. Spring Cloud 配置服务是一个 Spring Boot 应用程序， 所以你把它标记为 @SpringBootApplication。
 * 2. @EnableConfigServer 注解使服务的 main() 方法将服务作为 Spring Cloud 配置服务启动。
 * 3. main 方法启动服务并启动 Spring 容器。
 *
 * @author Jiajing Li
 * @date 2021-05-27 21:23:32
 */
@SuppressWarnings("all")
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}

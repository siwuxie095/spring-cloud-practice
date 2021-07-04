package com.siwuxie095.spring.cloud.confsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:23:32
 */
@SuppressWarnings("all")
// Spring Cloud Config 服务是 Spring Boot 应用程序，因此需要用 @SpringBootApplication 进行标记
@SpringBootApplication
// @EnableConfigServer 使服务成为 Spring Cloud Config 服务
@EnableConfigServer
public class ConfigServerApplication {

    // main 方法启动服务并启动 Spring 容器
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}

package com.siwuxie095.spring.cloud.eurekasvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:10:27
 */
@SuppressWarnings("all")
@SpringBootApplication
// 在 Spring 服务中启用 Eureka 服务器
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

}

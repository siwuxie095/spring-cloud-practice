package com.siwuxie095.spring.cloud.confsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:13:40
 */
@SuppressWarnings("all")
@EnableAutoConfiguration
@EnableEurekaClient
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}


package com.siwuxie095.spring.cloud.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:22:13
 */
@SuppressWarnings("all")
@SpringBootApplication
@EnableEurekaClient
//@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

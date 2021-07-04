package com.siwuxie095.spring.cloud.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jiajing Li
 * @date 2021-05-19 22:50:05
 */
@SuppressWarnings("all")
// 告诉 Spring Boot 框架，这是项目的引导类
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // 调用以启动整个 Spring Boot 服务
        SpringApplication.run(Application.class, args);
    }

}

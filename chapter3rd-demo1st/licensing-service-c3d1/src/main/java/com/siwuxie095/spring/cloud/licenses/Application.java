package com.siwuxie095.spring.cloud.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:34:24
 */
@SuppressWarnings("all")
@SpringBootApplication
@RefreshScope
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

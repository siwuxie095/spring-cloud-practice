package com.siwuxie095.spring.cloud.organization;

import com.siwuxie095.spring.cloud.organization.utils.UserContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.servlet.Filter;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:41:43
 */
@SuppressWarnings("all")
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableResourceServer
public class Application {
    @Bean
    public Filter userContextFilter() {
        UserContextFilter userContextFilter = new UserContextFilter();
        return userContextFilter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


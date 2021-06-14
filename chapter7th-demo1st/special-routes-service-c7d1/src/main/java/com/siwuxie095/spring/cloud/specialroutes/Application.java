package com.siwuxie095.spring.cloud.specialroutes;

import com.siwuxie095.spring.cloud.specialroutes.utils.UserContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * @author Jiajing Li
 * @date 2021-06-14 21:32:48
 */
@SuppressWarnings("all")
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
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


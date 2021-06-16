package com.siwuxie095.spring.cloud.licenses;

import com.siwuxie095.spring.cloud.licenses.config.ServiceConfig;
import com.siwuxie095.spring.cloud.licenses.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:27:50
 */
@SuppressWarnings("all")
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableResourceServer
public class Application {

    @Autowired
    private ServiceConfig serviceConfig;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Primary
    @Bean
    public RestTemplate getCustomRestTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}


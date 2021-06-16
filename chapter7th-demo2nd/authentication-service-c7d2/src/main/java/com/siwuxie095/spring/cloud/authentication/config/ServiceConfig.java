package com.siwuxie095.spring.cloud.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:05:41
 */
@SuppressWarnings("all")
@Component
@Configuration
public class ServiceConfig {
    @Value("${signing.key}")
    private String jwtSigningKey="";


    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

}

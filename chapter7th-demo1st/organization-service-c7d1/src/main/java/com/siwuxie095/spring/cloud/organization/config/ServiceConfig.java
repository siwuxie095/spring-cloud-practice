package com.siwuxie095.spring.cloud.organization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jiajing Li
 * @date 2021-06-14 21:13:11
 */
@SuppressWarnings("all")
@Component
public class ServiceConfig {
    @Value("${signing.key}")
    private String jwtSigningKey="";


    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

}

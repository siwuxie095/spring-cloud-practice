package com.siwuxie095.spring.cloud.licenses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:28:20
 */
@SuppressWarnings("all")
@Component
public class ServiceConfig{

    @Value("${example.property}")
    private String exampleProperty="";

    @Value("${redis.server}")
    private String redisServer="";

    @Value("${redis.port}")
    private String redisPort="";

    @Value("${signing.key}")
    private String jwtSigningKey="";


    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public String getExampleProperty(){
        return exampleProperty;
    }

    public String getRedisServer(){
        return redisServer;
    }

    public Integer getRedisPort(){
        return new Integer( redisPort ).intValue();
    }

}


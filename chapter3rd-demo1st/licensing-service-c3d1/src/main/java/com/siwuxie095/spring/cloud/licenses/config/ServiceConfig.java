package com.siwuxie095.spring.cloud.licenses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:29:00
 */
@SuppressWarnings("all")
@Component
public class ServiceConfig{

    @Value("${example.property}")
    private String exampleProperty;

    public String getExampleProperty(){
        return exampleProperty;
    }
}

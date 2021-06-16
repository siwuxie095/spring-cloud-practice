package com.siwuxie095.spring.cloud.organization.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:45:26
 */
@SuppressWarnings("all")
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/v1/organizations/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated();
    }
}


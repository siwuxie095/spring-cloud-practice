package com.siwuxie095.spring.cloud.licenses.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author Jiajing Li
 * @date 2021-06-26 16:58:22
 */
@SuppressWarnings("all")
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/v1/organizations/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated();
    }

}


package com.siwuxie095.spring.cloud.licenses.clients;

import com.siwuxie095.spring.cloud.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:39:55
 */
@SuppressWarnings("all")
@Component
public class OrganizationRestTemplateClient {

    @Autowired
    RestTemplate restTemplate;

    public Organization getOrganization(String organizationId){
        // 在使用支持 Ribbon 的 RestTemplate 时，使用 Eureka 服务 ID 来构建目标 URL
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://organizationservice/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }

}


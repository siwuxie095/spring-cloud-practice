package com.siwuxie095.spring.cloud.licenses.clients;

import com.siwuxie095.spring.cloud.licenses.model.Organization;
import com.siwuxie095.spring.cloud.licenses.utils.UserContext;
import com.siwuxie095.spring.cloud.licenses.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:33:39
 */
@SuppressWarnings("all")
@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    public Organization getOrganization(String organizationId){
        logger.debug("In Licensing Service.getOrganization: {}", UserContextHolder.getContext().getCorrelationId());

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://zuulserver:5555/api/organization/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}


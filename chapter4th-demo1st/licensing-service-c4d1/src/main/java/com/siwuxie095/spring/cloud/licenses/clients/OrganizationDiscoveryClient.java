package com.siwuxie095.spring.cloud.licenses.clients;

import com.siwuxie095.spring.cloud.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:38:43
 */
@SuppressWarnings("all")
@Component
public class OrganizationDiscoveryClient {

    // DiscoveryClient 被自动注入这个类
    @Autowired
    private DiscoveryClient discoveryClient;

    public Organization getOrganization(String organizationId) {
        RestTemplate restTemplate = new RestTemplate();
        // 获取组织服务的所有实例的列表
        List<ServiceInstance> instances =
                discoveryClient.getInstances("organizationservice");

        if (instances.size()==0) {
            return null;
        }
        // 检索要调用的服务端点
        String serviceUri = String.format("%s/v1/organizations/%s",
                instances.get(0).getUri().toString(),
                organizationId);
        System.out.println("!!!! SERVICE URI:  " + serviceUri);

        // 使用标准的 Spring REST 模板类去调用服务
        ResponseEntity< Organization > restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }

}


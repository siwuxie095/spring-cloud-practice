package com.siwuxie095.spring.cloud.licenses.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-06-05 22:51:48
 */
@SuppressWarnings("all")
@Service
public class DiscoveryService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public List getEurekaServices(){
        List<String> services = new ArrayList<String>();

        discoveryClient.getServices().forEach(serviceName -> {
            discoveryClient.getInstances(serviceName).forEach(instance->{
                services.add( String.format("%s:%s",serviceName,instance.getUri()));
            });
        });

        return services;
    }

}

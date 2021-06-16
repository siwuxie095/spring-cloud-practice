package com.siwuxie095.spring.cloud.licenses.controllers;

import com.siwuxie095.spring.cloud.licenses.services.DiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:36:06
 */
@SuppressWarnings("all")
@RestController
@RequestMapping(value="v1/tools")
public class ToolsController {
    @Autowired
    private DiscoveryService discoveryService;

    @RequestMapping(value="/eureka/services",method = RequestMethod.GET)
    public List<String> getEurekaServices() {
        return discoveryService.getEurekaServices();
    }

}


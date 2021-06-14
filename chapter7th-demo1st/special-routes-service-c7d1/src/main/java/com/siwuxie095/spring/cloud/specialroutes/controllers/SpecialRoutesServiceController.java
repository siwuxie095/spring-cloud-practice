package com.siwuxie095.spring.cloud.specialroutes.controllers;

import com.siwuxie095.spring.cloud.specialroutes.model.AbTestingRoute;
import com.siwuxie095.spring.cloud.specialroutes.services.AbTestingRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiajing Li
 * @date 2021-06-14 21:32:14
 */
@SuppressWarnings("all")
@RestController
@RequestMapping(value="v1/route/")
public class SpecialRoutesServiceController {

    @Autowired
    AbTestingRouteService routeService;

    @RequestMapping(value="abtesting/{serviceName}",method = RequestMethod.GET)
    public AbTestingRoute abstestings(@PathVariable("serviceName") String serviceName) {

        return routeService.getRoute( serviceName);
    }

}


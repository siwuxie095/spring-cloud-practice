package com.siwuxie095.spring.cloud.specialroutes.services;

import com.siwuxie095.spring.cloud.specialroutes.exception.NoRouteFound;
import com.siwuxie095.spring.cloud.specialroutes.model.AbTestingRoute;
import com.siwuxie095.spring.cloud.specialroutes.repository.AbTestingRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Jiajing Li
 * @date 2021-06-16 23:00:35
 */
@SuppressWarnings("all")
@Service
public class AbTestingRouteService {
    @Autowired
    private AbTestingRouteRepository abTestingRouteRepository;

    public AbTestingRoute getRoute(String serviceName) {
        AbTestingRoute route = abTestingRouteRepository.findByServiceName(serviceName);

        if (route==null){
            throw new NoRouteFound();
        }

        return route;
    }

    public void saveAbTestingRoute(AbTestingRoute route){

        abTestingRouteRepository.save(route);

    }

    public void updateRouteAbTestingRoute(AbTestingRoute route){
        abTestingRouteRepository.save(route);
    }

    public void deleteRoute(AbTestingRoute route){
        abTestingRouteRepository.delete(route.getServiceName());
    }
}


package com.siwuxie095.spring.cloud.specialroutes.repository;

import com.siwuxie095.spring.cloud.specialroutes.model.AbTestingRoute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jiajing Li
 * @date 2021-06-09 22:59:09
 */
@SuppressWarnings("all")
@Repository
public interface AbTestingRouteRepository extends CrudRepository<AbTestingRoute,String> {
    AbTestingRoute findByServiceName(String serviceName);
}

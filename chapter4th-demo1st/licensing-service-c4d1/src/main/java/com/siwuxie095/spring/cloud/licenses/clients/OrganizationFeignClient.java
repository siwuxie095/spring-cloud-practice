package com.siwuxie095.spring.cloud.licenses.clients;

import com.siwuxie095.spring.cloud.licenses.model.Organization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Jiajing Li
 * @date 2021-05-30 17:39:22
 */
@SuppressWarnings("all")
@FeignClient("organizationservice")
public interface OrganizationFeignClient {

    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organizations/{organizationId}",
            consumes="application/json")
    Organization getOrganization(@PathVariable("organizationId") String organizationId);

}

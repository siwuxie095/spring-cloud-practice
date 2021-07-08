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
// 使用 @FeignClient 注解标识服务
@FeignClient("organizationservice")
public interface OrganizationFeignClient {

    // 使用 @RequestMapping 注解来定义端点的路径和动作
    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organizations/{organizationId}",
            consumes="application/json")
    // 使用 @PathVariable 来定义传入端点的参数
    Organization getOrganization(@PathVariable("organizationId") String organizationId);

}

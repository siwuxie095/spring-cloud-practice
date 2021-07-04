package com.siwuxie095.spring.cloud.licenses.controllers;

import com.siwuxie095.spring.cloud.licenses.model.License;
import com.siwuxie095.spring.cloud.licenses.services.LicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Jiajing Li
 * @date 2021-05-19 22:53:35
 */
@SuppressWarnings("all")
// @Restcontroller 告诉 Spring Boot 这是一个基于 REST 的服务，它将自动序列化/反序列化服务请求/响应到 JSON
@RestController
// 在这个类中使用 /v1/organizations{organizationId}/licenses 的前缀，公开所有 HTTP 端点
@RequestMapping(value="v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {

    @Autowired
    private LicenseService licenseService;

    // 使用值创建一个 GET 端点 v1/organizations/{organizationId}/licenses/{licenseId}
    @RequestMapping(value="/{licenseId}",method = RequestMethod.GET)
    // 从 URL 映射两个参数（organizationId 和 licenseId）到方法参数
    public License getLicenses(@PathVariable("organizationId") String organizationId,
                               @PathVariable("licenseId") String licenseId) {
        //return licenseService.getLicense(licenseId);
        return new License()
                .withId(licenseId)
                .withOrganizationId(organizationId)
                .withProductName("Teleco")
                .withLicenseType("Seat")
                .withOrganizationId("TestOrg");
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.PUT)
    public String updateLicenses( @PathVariable("licenseId") String licenseId) {
        return String.format("This is the put");
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.POST)
    public String saveLicenses( @PathVariable("licenseId") String licenseId) {
        return String.format("This is the post");
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteLicenses( @PathVariable("licenseId") String licenseId) {
        return String.format("This is the Delete");
    }

}


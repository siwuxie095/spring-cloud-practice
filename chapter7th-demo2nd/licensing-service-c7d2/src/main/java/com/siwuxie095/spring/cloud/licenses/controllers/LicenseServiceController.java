package com.siwuxie095.spring.cloud.licenses.controllers;

import com.siwuxie095.spring.cloud.licenses.config.ServiceConfig;
import com.siwuxie095.spring.cloud.licenses.model.License;
import com.siwuxie095.spring.cloud.licenses.services.LicenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:35:36
 */
@SuppressWarnings("all")
@RestController
@RequestMapping(value="v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {
    @Autowired
    private LicenseService licenseService;

    @Autowired
    private ServiceConfig serviceConfig;

    @Autowired
    private HttpServletRequest request;

    private static final Logger logger = LoggerFactory.getLogger(LicenseServiceController.class);


    @RequestMapping(value="/",method = RequestMethod.GET)
    public List<License> getLicenses(@PathVariable("organizationId") String organizationId) {

        return licenseService.getLicensesByOrg(organizationId);
    }

    @RequestMapping(value="/{licenseId}",method = RequestMethod.GET)
    public License getLicenses( @PathVariable("organizationId") String organizationId,
                                @PathVariable("licenseId") String licenseId) {
        logger.debug("Entering the license-service-controller");
        logger.debug("Found tmx-correlation-id in license-service-controller: {} ", request.getHeader("tmx-correlation-id"));
        return licenseService.getLicense(organizationId, licenseId);
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.PUT)
    public void updateLicenses( @PathVariable("licenseId") String licenseId, @RequestBody License license) {
        licenseService.updateLicense(license);
    }

    @RequestMapping(value="/",method = RequestMethod.POST)
    public void saveLicenses(@RequestBody License license) {
        licenseService.saveLicense(license);
    }

    @RequestMapping(value="{licenseId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLicenses( @PathVariable("licenseId") String licenseId, @RequestBody License license) {
        licenseService.deleteLicense(license);
    }
}


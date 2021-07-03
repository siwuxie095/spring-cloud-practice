package com.siwuxie095.spring.cloud.licenses.services;

import com.siwuxie095.spring.cloud.licenses.model.License;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Jiajing Li
 * @date 2021-05-19 22:52:18
 */
@SuppressWarnings("all")
@Service
public class LicenseService {

    public License getLicense(String licenseId){
        return new License()
                .withId(licenseId)
                .withOrganizationId( UUID.randomUUID().toString() )
                .withProductName("Test Product Name")
                .withLicenseType("PerSeat");
    }

    public void saveLicense(License license){

    }

    public void updateLicense(License license){

    }

    public void deleteLicense(License license){

    }

}


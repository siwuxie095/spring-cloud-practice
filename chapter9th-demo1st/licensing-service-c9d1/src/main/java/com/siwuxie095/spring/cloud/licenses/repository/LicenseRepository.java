package com.siwuxie095.spring.cloud.licenses.repository;

import com.siwuxie095.spring.cloud.licenses.model.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-06-23 08:17:19
 */
@SuppressWarnings("all")
@Repository
public interface LicenseRepository extends CrudRepository<License,String> {
    List<License> findByOrganizationId(String organizationId);
    License findByOrganizationIdAndLicenseId(String organizationId,String licenseId);
}
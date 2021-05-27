package com.siwuxie095.spring.cloud.licenses.repository;

import com.siwuxie095.spring.cloud.licenses.model.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:30:50
 */
@SuppressWarnings("all")
@Repository
public interface LicenseRepository extends CrudRepository<License,String> {

    public List<License> findByOrganizationId(String organizationId);

    public License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}

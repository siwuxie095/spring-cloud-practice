package com.siwuxie095.spring.cloud.organization.repository;

import com.siwuxie095.spring.cloud.organization.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jiajing Li
 * @date 2021-06-16 22:52:48
 */
@SuppressWarnings("all")
@Repository
public interface OrganizationRepository extends CrudRepository<Organization,String> {
    Organization findById(String organizationId);
}

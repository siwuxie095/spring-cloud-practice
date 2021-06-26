package com.siwuxie095.spring.cloud.licenses.repository;

import com.siwuxie095.spring.cloud.licenses.model.Organization;

/**
 * @author Jiajing Li
 * @date 2021-06-26 16:55:23
 */
@SuppressWarnings("all")
public interface OrganizationRedisRepository {
    void saveOrganization(Organization org);
    void updateOrganization(Organization org);
    void deleteOrganization(String organizationId);
    Organization findOrganization(String organizationId);
}

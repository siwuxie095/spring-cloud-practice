package com.siwuxie095.spring.cloud.organization.services;

import com.siwuxie095.spring.cloud.organization.events.source.SimpleSourceBean;
import com.siwuxie095.spring.cloud.organization.model.Organization;
import com.siwuxie095.spring.cloud.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Jiajing Li
 * @date 2021-06-21 08:24:35
 */
@SuppressWarnings("all")
@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    public Organization getOrg(String organizationId) {
        return orgRepository.findById(organizationId);
    }

    public void saveOrg(Organization org){
        org.setId(UUID.randomUUID().toString());

        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());

    }

    public void deleteOrg(String  orgId){
        orgRepository.delete(orgId);
        simpleSourceBean.publishOrgChange("DELETE", orgId);
    }

}


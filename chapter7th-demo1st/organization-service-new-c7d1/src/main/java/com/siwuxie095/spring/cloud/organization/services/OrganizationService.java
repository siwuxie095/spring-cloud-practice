package com.siwuxie095.spring.cloud.organization.services;

import com.siwuxie095.spring.cloud.organization.model.Organization;
import com.siwuxie095.spring.cloud.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Jiajing Li
 * @date 2021-06-14 21:24:25
 */
@SuppressWarnings("all")
@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    public Organization getOrg(String organizationId) {
        return orgRepository.findById(organizationId);
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);

    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
    }

    public void deleteOrg(Organization org){
        orgRepository.delete( org.getId());
    }
}


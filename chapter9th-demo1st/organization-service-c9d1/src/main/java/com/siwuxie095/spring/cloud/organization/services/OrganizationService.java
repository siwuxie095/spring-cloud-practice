package com.siwuxie095.spring.cloud.organization.services;

import com.siwuxie095.spring.cloud.organization.events.source.SimpleSourceBean;
import com.siwuxie095.spring.cloud.organization.model.Organization;
import com.siwuxie095.spring.cloud.organization.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Jiajing Li
 * @date 2021-06-23 08:36:12
 */
@SuppressWarnings("all")
@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private Tracer tracer;

    @Autowired
    SimpleSourceBean simpleSourceBean;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    public Organization getOrg
            (String organizationId) {
        Span newSpan = tracer.createSpan("getOrgDBCall");

        logger.debug("In the organizationService.getOrg() call");
        try {
            return orgRepository.findById(organizationId);
        }
        finally{
            newSpan.tag("peer.service", "postgres");
            newSpan.logEvent(org.springframework.cloud.sleuth.Span.CLIENT_RECV);
            tracer.close(newSpan);
        }
    }

    public void saveOrg(Organization org){
        org.setId( UUID.randomUUID().toString());

        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(Organization org){
        orgRepository.save(org);
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());

    }

    public void deleteOrg(String orgId){
        orgRepository.delete( orgId );
        simpleSourceBean.publishOrgChange("DELETE", orgId);
    }
}


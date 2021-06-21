package com.siwuxie095.spring.cloud.licenses.events.handlers;

import com.siwuxie095.spring.cloud.licenses.events.CustomChannels;
import com.siwuxie095.spring.cloud.licenses.events.models.OrganizationChangeModel;
import com.siwuxie095.spring.cloud.licenses.repository.OrganizationRedisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * @author Jiajing Li
 * @date 2021-06-21 08:16:41
 */
@SuppressWarnings("all")
@EnableBinding(CustomChannels.class)
public class OrganizationChangeHandler {

    @Autowired
    private OrganizationRedisRepository organizationRedisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationChangeHandler.class);

    @StreamListener("inboundOrgChanges")
    public void loggerSink(OrganizationChangeModel orgChange) {
        logger.debug("Received a message of type " + orgChange.getType());
        switch(orgChange.getAction()){
            case "GET":
                logger.debug("Received a GET event from the organization service for organization id {}", orgChange.getOrganizationId());
                break;
            case "SAVE":
                logger.debug("Received a SAVE event from the organization service for organization id {}", orgChange.getOrganizationId());
                break;
            case "UPDATE":
                logger.debug("Received a UPDATE event from the organization service for organization id {}", orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            case "DELETE":
                logger.debug("Received a DELETE event from the organization service for organization id {}", orgChange.getOrganizationId());
                organizationRedisRepository.deleteOrganization(orgChange.getOrganizationId());
                break;
            default:
                logger.error("Received an UNKNOWN event from the organization service of type {}", orgChange.getType());
                break;

        }
    }

}


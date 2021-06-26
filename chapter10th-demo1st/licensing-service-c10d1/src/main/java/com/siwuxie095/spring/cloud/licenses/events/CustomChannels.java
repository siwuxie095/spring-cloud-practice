package com.siwuxie095.spring.cloud.licenses.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Jiajing Li
 * @date 2021-06-26 16:59:57
 */
@SuppressWarnings("all")
public interface CustomChannels {

    @Input("inboundOrgChanges")
    SubscribableChannel orgs();

}

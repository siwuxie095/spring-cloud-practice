package com.siwuxie095.spring.cloud.zuulsvr.model;

/**
 * @author Jiajing Li
 * @date 2021-06-21 08:31:50
 */
@SuppressWarnings("all")
public class UserInfo {
    String organizationId;
    String userId;

    public String getOrganizationId() {
        return this.organizationId;
    }


    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}


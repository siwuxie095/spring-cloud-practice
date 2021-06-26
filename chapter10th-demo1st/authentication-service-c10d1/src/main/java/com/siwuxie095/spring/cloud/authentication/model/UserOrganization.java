package com.siwuxie095.spring.cloud.authentication.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Jiajing Li
 * @date 2021-06-26 16:31:34
 */
@SuppressWarnings("all")
@Entity
@Table(name = "user_orgs")
public class UserOrganization implements Serializable {
    @Column(name = "organization_id", nullable = false)
    String organizationId;

    @Id
    @Column(name = "user_name", nullable = false)
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }


}


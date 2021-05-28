package com.siwuxie095.spring.cloud.licenses.model;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:30:13
 */
@SuppressWarnings("all")
public class Organization {
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }


}

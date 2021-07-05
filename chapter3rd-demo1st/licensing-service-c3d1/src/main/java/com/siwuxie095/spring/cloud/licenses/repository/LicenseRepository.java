package com.siwuxie095.spring.cloud.licenses.repository;

import com.siwuxie095.spring.cloud.licenses.model.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jiajing Li
 * @date 2021-05-27 21:30:50
 */
@SuppressWarnings("all")
// 告诉 Spring Boot 这是一个 JPA 存储库类
@Repository
// 定义正在扩展 Spring CrudRepository
public interface LicenseRepository extends CrudRepository<License,String> {

    // 每个查询方法被 Spring 解析为 SELECT...FROM 查询
    public List<License> findByOrganizationId(String organizationId);

    public License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}

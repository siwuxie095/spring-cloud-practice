package com.siwuxie095.spring.cloud.authentication.repository;

import com.siwuxie095.spring.cloud.authentication.model.UserOrganization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jiajing Li
 * @date 2021-06-14 20:43:42
 */
@SuppressWarnings("all")
@Repository
public interface OrgUserRepository extends CrudRepository<UserOrganization,String> {
    UserOrganization findByUserName(String userName);
}

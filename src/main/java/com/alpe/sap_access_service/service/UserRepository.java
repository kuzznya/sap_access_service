package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.ws.rs.QueryParam;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT DISTINCT u FROM User u WHERE u.accessToken = :accessToken")
    User getUserByAccessToken(@QueryParam("accessToken") String accessToken);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM User u WHERE TIMESTAMPDIFF(SECOND, u.lastTimeAccessed, now()) > :seconds_diff")
    void deleteInactiveUsers(@Param("seconds_diff") int secondsDiff);
}

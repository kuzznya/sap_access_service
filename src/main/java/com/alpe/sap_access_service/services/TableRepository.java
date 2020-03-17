package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.SAPTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<SAPTableEntity, Long> {
    @Query(value = "SELECT t FROM SAPTableEntity t WHERE t.accessToken = :access_token AND t.paramsHash = :params_hash")
    SAPTableEntity findSAPTableEntityByAccessTokenAndParamsHash(@Param("access_token") String accessToken,
                                                                @Param("params_hash") Integer paramsHash);
}

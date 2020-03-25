package com.alpe.sap_access_service.service;

import com.alpe.sap_access_service.model.SAPTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TableRepository extends JpaRepository<SAPTableEntity, Long> {

    //TODO edit query to select tables witl null params
    @Query(value = "SELECT t FROM SAPTableEntity t WHERE t.accessToken = :access_token AND t.name = :name AND t.language = :language AND t.where = :where AND t.order = :order AND t.group = :group AND t.fieldNames = :field_names")
    SAPTableEntity findSAPTableEntityByAccessTokenAndParams(@Param("access_token") String accessToken,
                                                            @Param("name") String tableName,
                                                            @Param("language") Character language,
                                                            @Param("where") String where,
                                                            @Param("order") String order,
                                                            @Param("group") String group,
                                                            @Param("field_names") String fieldNames);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM SAPTableEntity t WHERE TIMESTAMPDIFF(SECOND, t.creationDate, now()) > :seconds_diff")
    void deleteOldSAPTableEntities(@Param("seconds_diff") int secondsDiff);
}

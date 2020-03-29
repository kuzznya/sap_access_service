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

    @Query(value = "SELECT DISTINCT t FROM SAPTableEntity t WHERE t.accessToken = :access_token AND t.name = :name AND " +
            "(:language IS NULL OR t.language = :language) AND (:where IS NULL OR t.where = :where) " +
            "AND (:order IS NULL OR t.order = :order) AND (:group IS NULL OR t.group = :group) AND " +
            "(:field_names IS NULL OR t.fieldNames = :field_names)")
    SAPTableEntity findSAPTableEntityByAccessTokenAndParams(@Param("access_token") String accessToken,
                                                            @Param("name") String tableName,
                                                            @Param("language") Character language,
                                                            @Param("where") String where,
                                                            @Param("order") String order,
                                                            @Param("group") String group,
                                                            @Param("field_names") String fieldNames);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM SAPTableEntity t WHERE TIMESTAMPDIFF(SECOND, t.updateDate, now()) > :seconds_diff")
    void deleteOldSAPTableEntities(@Param("seconds_diff") int secondsDiff);
}

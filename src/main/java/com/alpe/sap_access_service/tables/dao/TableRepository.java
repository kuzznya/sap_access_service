package com.alpe.sap_access_service.tables.dao;

import com.alpe.sap_access_service.tables.model.SAPTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TableRepository extends JpaRepository<SAPTableEntity, Long> {
    SAPTableEntity findByUserIdAndNameAndLanguageAndWhereAndOrderAndGroupAndFieldNames(long userId,
                                                                                       String name,
                                                                                       Character language,
                                                                                       String where,
                                                                                       String order,
                                                                                       String group,
                                                                                       String fieldNames);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM SAPTableEntity t WHERE TIMESTAMPDIFF(SECOND, t.updateDate, now()) > :seconds_diff")
    void deleteOldSAPTableEntities(@Param("seconds_diff") int secondsDiff);
}

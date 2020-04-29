package com.alpe.sap_access_service.charts.dao;

import com.alpe.sap_access_service.charts.model.ChartDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Repository
public interface ChartDataRepository extends JpaRepository<ChartDataEntity, Long> {

    ChartDataEntity findOneByTableNameAndValuesColumnAndCategoriesColumnAndCaptionsColumn(@NotNull String tableName,
                                                                                          @NotNull String valuesColumn,
                                                                                          String categoriesColumn,
                                                                                          String captionsColumn);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM ChartDataEntity c WHERE TIMESTAMPDIFF(SECOND, c.updateDate, now()) > :seconds_diff")
    void deleteOldChartDataEntities(@Param("seconds_diff") int secondsDiff);
}

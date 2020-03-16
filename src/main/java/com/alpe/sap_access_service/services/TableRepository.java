package com.alpe.sap_access_service.services;

import com.alpe.sap_access_service.model.SAPTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<SAPTableEntity, Long> {
}

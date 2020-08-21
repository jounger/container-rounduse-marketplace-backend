package com.crm.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Report;
import com.crm.models.Supplier;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

  Page<Report> findBySender(Supplier sender, Pageable pageable);

  @Query(value = "SELECT COUNT(r) FROM Report r WHERE r.createdAt > :startDate AND r.createdAt < :endDate")
  Integer countReportByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}

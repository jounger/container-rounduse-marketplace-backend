package com.crm.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Outbound;

@Repository
public interface OutboundRepository extends JpaRepository<Outbound, Long>, JpaSpecificationExecutor<Outbound> {

  Page<Outbound> findByStatus(String status, Pageable pageable);

  @Query(value = "SELECT o FROM Outbound o WHERE o.merchant.username = :username")
  Page<Outbound> findByMerchant(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT o FROM Outbound o WHERE o.merchant.username = :username AND o.status = :status")
  Page<Outbound> findByMerchant(@Param("username") String username, @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT DISTINCT o FROM Outbound o LEFT JOIN o.biddingDocuments bd"
      + " LEFT JOIN bd.bids b LEFT JOIN b.combined c WHERE c.id = :id")
  Optional<Outbound> findByCombined(@Param("id") Long combinedId);

  @Query(value = "SELECT COUNT(o) FROM Outbound o" + " WHERE o.createdAt > :startDate AND o.createdAt < :endDate")
  Integer countOutbounds(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(o) FROM Outbound o" + " WHERE o.status in :statusList"
      + " AND o.createdAt > :startDate AND o.createdAt < :endDate")
  Integer countOutbounds(@Param("statusList") List<String> statusList, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(o) FROM Outbound o WHERE o.merchant.username = :username"
      + " AND o.createdAt > :startDate AND o.createdAt < :endDate")
  Integer countOutbounds(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(o) FROM Outbound o"
      + " WHERE o.merchant.username = :username AND o.status in :statusList"
      + " AND o.createdAt > :startDate AND o.createdAt < :endDate")
  Integer countOutbounds(@Param("username") String username, @Param("statusList") List<String> statusList,
      @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}

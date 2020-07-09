package com.crm.repository;

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

  boolean existsById(Long id);

  Optional<Outbound> findById(Long id);

  @Query(value = "SELECT o FROM Outbound o WHERE o.status = :status")
  Page<Outbound> findAll(@Param("status") String status, Pageable pageable);

  @Query(value = "SELECT o FROM Outbound o WHERE o.merchant.id = :id")
  Page<Outbound> findByMerchantId(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT o FROM Outbound o WHERE o.merchant.id = :id AND o.status = :status")
  Page<Outbound> findByMerchantId(@Param("id") Long id, @Param("status") String status, Pageable pageable);

}

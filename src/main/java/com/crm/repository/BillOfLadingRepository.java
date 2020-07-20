package com.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.BillOfLading;

@Repository
public interface BillOfLadingRepository
    extends JpaRepository<BillOfLading, Long>, JpaSpecificationExecutor<BillOfLading> {

  Boolean existsByBillOfLadingNumber(String billOfLadingNumber);

  @Query(value = "SELECT b FROM BillOfLading b WHERE b.inbound.id = :id")
  Page<BillOfLading> findByInbound(@Param("id") Long id, Pageable pageable);

  Optional<BillOfLading> findByBillOfLadingNumber(String billOfLadingNumber);

  @Query(value = "SELECT b FROM BillOfLading b WHERE b.inbound.forwarder.id = :id")
  List<BillOfLading> findByForwarder(Long id);
}

package com.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.BillOfLading;

@Repository
public interface BillOfLadingRepository
    extends JpaRepository<BillOfLading, Long>, JpaSpecificationExecutor<BillOfLading> {

  Boolean existsByNumber(String number);

  @Query(value = "SELECT b FROM BillOfLading b WHERE b.inbound.id = :id")
  Optional<BillOfLading> findByInbound(@Param("id") Long id);

  Optional<BillOfLading> findByNumber(String number);

  @Query(value = "SELECT b FROM BillOfLading b WHERE b.inbound.forwarder.id = :id")
  List<BillOfLading> findByForwarder(Long id);
}

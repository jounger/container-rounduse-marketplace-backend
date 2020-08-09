package com.crm.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Forwarder;

@Repository
public interface ForwarderRepository extends JpaRepository<Forwarder, Long> {

  Optional<Forwarder> findByUsername(String username);

  @Query(value = "SELECT DISTINCT f FROM Forwarder f JOIN f.inbounds i JOIN i.billOfLading b"
      + " WHERE i.shippingLine.companyCode = :shippingLine"
      + " AND i.containerType.name = :containerType AND b.freeTime > :cutOffTime"
      + " AND i.emptyTime < :packingTime AND b.freeTime > :packingTime")
  Page<Forwarder> findByOutbound(@Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, @Param("packingTime") LocalDateTime packingTime,
      @Param("cutOffTime") LocalDateTime cutOffTime, Pageable pageable);

  boolean existsByUsername(String username);
}

package com.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Inbound;

@Repository
public interface InboundRepository extends JpaRepository<Inbound, Long> {

  @Query(value = "SELECT i FROM Inbound i WHERE i.forwarder.id = :id")
  Page<Inbound> findInboundsByFowarder(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT i FROM Inbound i LEFT JOIN i.billOfLading.containers c "
      + "WHERE i.forwarder.id = :id "
      + "AND (i.billOfLading.freeTime < :freeTime OR i.pickupTime > :pickupTime) "
      + "AND c.containerNumber = :containerNumber "
      + "AND c.id != :containerId")
  List<Inbound> checkInboundsByFowarder(@Param("id") Long id, @Param("pickupTime") LocalDateTime pickupTime,
      @Param("freeTime") LocalDateTime freeTime, @Param("containerNumber") String containerNumber,
      @Param("containerId") Long containerId);

  /*
   * @param shippingLine is companyCode of ShippingLine Entity
   * 
   * @param containerType is name of ContainerType Entity
   * 
   */
  @Query(value = "SELECT i FROM Inbound i WHERE i.shippingLine.companyCode = :shippingLine AND i.containerType.name = :containerType")
  Page<Inbound> findInboundsByOutbound(@Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, Pageable pageable);

  /*
   * @param shippingLine is companyCode of ShippingLine Entity
   * 
   * @param containerType is name of ContainerType Entity
   * 
   * @param
   * 
   */
  @Query(value = "SELECT i FROM Inbound i WHERE i.forwarder.id = :userId AND i.shippingLine.companyCode = :shippingLine AND i.containerType.name = :containerType")
  Page<Inbound> findInboundsByOutboundAndForwarder(@Param("userId") Long userId,
      @Param("shippingLine") String shippingLine, @Param("containerType") String containerType, Pageable pageable);
}

package com.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Inbound;

@Repository
public interface InboundRepository extends JpaRepository<Inbound, Long>, JpaSpecificationExecutor<Inbound> {

  @Query(value = "SELECT i FROM Inbound i WHERE i.forwarder.id = :id")
  Page<Inbound> findByFowarder(@Param("id") Long id, Pageable pageable);

  /*
   * @param shippingLine is companyCode of ShippingLine Entity
   * 
   * @param containerType is name of ContainerType Entity
   * 
   */
  @Query(value = "SELECT i FROM Inbound i WHERE i.shippingLine.companyCode = :shippingLine AND i.containerType.name = :containerType")
  Page<Inbound> findByOutbound(@Param("shippingLine") String shippingLine, @Param("containerType") String containerType,
      Pageable pageable);

  @Query(value = "FROM Inbound i LEFT JOIN i.billOfLading b LEFT JOIN b.containers c WHERE i.shippingLine.companyCode = :shippingLine"
      + " AND i.containerType.name = :containerType" + " AND c.status IN :status" + " AND i.emptyTime < :packingTime"
      + " AND i.billOfLading.freeTime > :cutOffTime")
  Page<Inbound> findByOutbound(@Param("shippingLine") String shippingLine, @Param("containerType") String containerType,
      @Param("status") List<String> status, @Param("packingTime") LocalDateTime packingTime,
      @Param("cutOffTime") LocalDateTime cutOffTime, Pageable pageable);

  /*
   * @param shippingLine is companyCode of ShippingLine Entity
   * 
   * @param containerType is name of ContainerType Entity
   * 
   * @param
   * 
   */
  @Query(value = "SELECT i FROM Inbound i WHERE i.forwarder.id = :userId AND i.shippingLine.companyCode = :shippingLine AND i.containerType.name = :containerType")
  Page<Inbound> findByOutboundAndForwarder(@Param("userId") Long userId, @Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, Pageable pageable);
}

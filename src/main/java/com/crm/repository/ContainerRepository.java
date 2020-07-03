package com.crm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

  Boolean existsByContainerNumber(String containerNumber);

  Boolean existsByLicensePlate(String licensePlate);

  @Query(value = "SELECT b FROM Container b WHERE b.billOfLading.id = :id")
  Page<Container> getContainersByBillOfLading(@Param("id") Long id, Pageable pageable);

  /*
   * @param containerType as ContainerType.name
   * 
   * @param shippingLine as ShippingLine.companycode
   * 
   * @param portOfLoading as PortOfLoading.nameCode
   */
  @Query(value = "FROM Container c" + " WHERE c.billOfLading.inbound.shippingLine.companyCode = :shippingLine"
      + " AND c.billOfLading.inbound.containerType.name = :containerType" + " AND c.status IN :status"
      + " AND c.billOfLading.inbound.emptyTime < :packingTime" + " AND c.billOfLading.freeTime > :cutOffTime"
      + " AND c.billOfLading.portOfDelivery.nameCode = :portOfLoading")
  List<Container> findByOutbound(@Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, @Param("status") List<String> status,
      @Param("packingTime") LocalDateTime packingTime, @Param("cutOffTime") LocalDateTime cutOffTime,
      @Param("portOfLoading") String portOfLoading);

  @Query(value = "SELECT b FROM Container b WHERE b.billOfLading.inbound.id = :id")
  Page<Container> getContainersByInbound(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT d FROM Container d WHERE d.driver.id = :id")
  List<Container> findByDriver(@Param("id") Long id);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.bid.biddingDocument.id = :id AND c.status = 'COMBINED'")
  int getCombinedContainersByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT b FROM Container b WHERE b.bid.id = :id")
  Page<Container> getContainersByBid(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT b FROM Container b WHERE b.bid.id = :id AND b.status = :status")
  Page<Container> getContainersByBid(@Param("id") Long id, @Param("status") String status, Pageable pageable);
}

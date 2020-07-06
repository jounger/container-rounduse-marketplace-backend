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
  Page<Container> findContainersByBillOfLading(@Param("id") Long id, Pageable pageable);

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

  @Query(value = "SELECT c FROM Container c WHERE c.billOfLading.inbound.id = :id")
  Page<Container> findContainersByInbound(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Container c WHERE c.driver.id = :id")
  List<Container> findByDriver(@Param("id") Long id);

  @Query(value = "SELECT COUNT(*) FROM Container c LEFT JOIN c.bids b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id AND c.status = 'COMBINED'")
  long countCombinedContainersByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.bids b WHERE b.id = :id")
  Page<Container> findContainersByBid(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.bids b WHERE b.id = :id AND c.status = :status")
  Page<Container> findContainersByBid(@Param("id") Long id, @Param("status") String status, Pageable pageable);

}

package com.crm.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
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

  Boolean existsByNumber(String number);

  @Query(value = "SELECT b FROM Container b WHERE b.billOfLading.id = :id")
  Page<Container> findByBillOfLading(@Param("id") Long id, Pageable pageable);

  /*
   * @param containerType as ContainerType.name
   * 
   * @param shippingLine as ShippingLine.companycode
   * 
   * @param portOfLoading as PortOfLoading.nameCode
   */
  @Query(value = "FROM Container c" + " WHERE c.billOfLading.inbound.shippingLine.companyCode = :shippingLine"
      + " AND c.billOfLading.inbound.containerType.name = :containerType" + " AND c.status IN :status"
      + " AND c.billOfLading.inbound.emptyTime < :packingTime" + " AND c.billOfLading.freeTime > :cutOffTime")
  List<Container> findByOutbound(@Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, @Param("status") List<String> status,
      @Param("packingTime") LocalDateTime packingTime, @Param("cutOffTime") LocalDateTime cutOffTime);

  @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM Container c"
      + " WHERE c.billOfLading.inbound.shippingLine.companyCode = :shippingLine"
      + " AND c.billOfLading.inbound.containerType.name = :containerType" + " AND c.status IN :status"
      + " AND c.billOfLading.inbound.emptyTime < :packingTime" + " AND c.billOfLading.freeTime > :cutOffTime"
      + " AND c.id = :id")
  Boolean existsByOutbound(@Param("id") Long id, @Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, @Param("status") List<String> status,
      @Param("packingTime") LocalDateTime packingTime, @Param("cutOffTime") LocalDateTime cutOffTime);

  @Query(value = "SELECT c FROM Container c WHERE c.billOfLading.inbound.id = :id")
  Page<Container> findContainersByInbound(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Container c WHERE c.driver.id = :driverId")
  List<Container> findByDriver(@Param("driverId") Long driverId);

  @Query(value = "SELECT c FROM Container c WHERE c.trailer.id = :trailerId")
  List<Container> findByTrailer(@Param("trailerId") Long trailerId);

  @Query(value = "SELECT c FROM Container c WHERE c.tractor.id = :tractorId")
  List<Container> findByTractor(@Param("tractorId") Long tractorId);

  @Query(value = "SELECT COUNT(*) FROM Container c LEFT JOIN c.bids b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id AND c.status = 'COMBINED'")
  long countCombinedContainersByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.bids b WHERE b.id = :id")
  Page<Container> findByBid(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.bids b WHERE b.id = :id AND c.status = :status")
  Page<Container> findByBid(@Param("id") Long id, @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.trailer t WHERE t.id = :id AND (c.status = :statusCombined OR c.status = :statusBidding)")
  Collection<Container> findByTrailer(@Param("id") Long id, @Param("statusCombined") String statusCombined,
      @Param("statusBidding") String statusBidding);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.tractor t WHERE t.id = :id AND (c.status = :statusCombined OR c.status = :statusBidding)")
  Collection<Container> findByTractor(@Param("id") Long id, @Param("statusCombined") String statusCombined,
      @Param("statusBidding") String statusBidding);

  /* Check time busy Container */

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username " + "AND c.number = :number "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime))")
  boolean findByNumber(@Param("number") String number, @Param("pickupTime") LocalDateTime pickupTime,
      @Param("freeTime") LocalDateTime freeTime, @Param("username") String username);

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username " + "AND c.number = :number "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.billOfLading.id != :id")
  boolean findByNumber(@Param("id") Long id, @Param("username") String username, @Param("number") String number,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime);

  /* Check time busy Driver */

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username " + "AND c.driver.id = :driverId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.status != :status")
  boolean findByDriver(@Param("driverId") Long driverId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime,
      @Param("status") String status);

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username " + "AND c.driver.id = :driverId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.billOfLading.id != :id " + "AND c.status != :status")
  boolean findByDriver(@Param("driverId") Long driverId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime, @Param("id") Long id,
      @Param("status") String status);

  /* Check time busy Trailer */

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username "
      + "AND c.trailer.id = :trailerId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.status != :status")
  boolean findByTrailer(@Param("trailerId") Long trailerId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime,
      @Param("status") String status);

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username "
      + "AND c.trailer.id = :trailerId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.billOfLading.id != :id " + "AND c.status != :status")
  boolean findByTrailer(@Param("trailerId") Long trailerId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime, @Param("id") Long id,
      @Param("status") String status);

  /* Check time busy Tractor */

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username "
      + "AND c.tractor.id = :tractorId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.status != :status")
  boolean findByTractor(@Param("tractorId") Long tractorId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime,
      @Param("status") String status);

  @Query(value = "SELECT CASE WHEN COUNT(c) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c WHERE c.billOfLading.inbound.forwarder.username = :username "
      + "AND c.tractor.id = :tractorId "
      + "AND ((c.billOfLading.freeTime > :freeTime AND c.billOfLading.inbound.pickupTime < :freeTime) "
      + "OR (c.billOfLading.freeTime > :pickupTime AND c.billOfLading.inbound.pickupTime < :pickupTime) "
      + "OR (c.billOfLading.freeTime < :freeTime AND c.billOfLading.inbound.pickupTime > :pickupTime) "
      + "OR (c.billOfLading.freeTime = :freeTime) " + "OR (c.billOfLading.inbound.pickupTime = :pickupTime) "
      + "OR (c.billOfLading.inbound.pickupTime = :freeTime) " + "OR (c.billOfLading.freeTime = :pickupTime)) "
      + "AND c.billOfLading.id != :id " + "AND c.status != :status")
  boolean findByTractor(@Param("tractorId") Long tractorId, @Param("username") String username,
      @Param("pickupTime") LocalDateTime pickupTime, @Param("freeTime") LocalDateTime freeTime, @Param("id") Long id,
      @Param("status") String status);

  @Query(value = "SELECT c FROM Container c LEFT JOIN c.bids b WHERE b.id = :id AND c.status = :status")
  List<Container> findByBidAndStatus(@Param("id") Long id, @Param("status") String status);

  @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END "
      + "FROM Container c LEFT JOIN c.bids b WHERE c.id = :id AND b.id = :bidId")
  boolean isContainedByBid(@Param("id") Long id, @Param("bidId") Long bidId);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainersByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.status IN :statusList"
      + " AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainersByOperator(@Param("statusList") List<String> statusList, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.driver.forwarder.username = :username"
      + " AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainers(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.driver.forwarder.username = :username"
      + " AND c.status IN :statusList AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainers(@Param("username") String username, @Param("statusList") List<String> statusList,
      @Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.billOfLading.inbound.shippingLine.username = :username"
      + " AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainersByShippingLine(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(c) FROM Container c WHERE c.billOfLading.inbound.shippingLine.username = :username"
      + " AND c.status IN :statusList AND c.createdAt > :startDate AND c.createdAt < :endDate")
  Integer countContainersByShippingLine(@Param("username") String username,
      @Param("statusList") List<String> statusList, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}

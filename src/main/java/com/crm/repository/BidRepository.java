package com.crm.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Bid;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

  @Query(value = "SELECT b FROM Bid b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id "
      + "AND (b.bidder.username = :username OR bd.offeree.username = :username)")
  Page<Bid> findByBiddingDocument(@Param("id") Long id, @Param("username") String username, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id "
      + "AND (b.bidder.username = :username OR bd.offeree.username = :username) AND b.status = :status")
  Page<Bid> findByBiddingDocument(@Param("id") Long id, @Param("username") String username,
      @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id "
      + "AND (bd.offeree.username = :username or b.bidder.username = :username) AND b.combined IS NOT NULL")
  Page<Bid> findByBiddingDocumentAndExistCombined(@Param("id") Long id, @Param("username") String username,
      Pageable pageable);

  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.username = :username")
  Page<Bid> findByForwarder(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.username = :username AND b.status = :status")
  Page<Bid> findByForwarder(@Param("username") String username, @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(b) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Bid b JOIN b.containers c WHERE b.biddingDocument.id = :id AND c.status != 'DELIVERED'")
  boolean isAllCombinedByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT CASE WHEN COUNT(b) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Bid b JOIN b.containers c WHERE b.biddingDocument.id = :id AND c.status != 'COMBINED'")
  boolean isAllAcceptedByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.createdAt > :startDate AND b.createdAt < :endDate")
  Integer countBidsByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.status = :statusList"
      + " AND b.createdAt > :startDate AND b.createdAt < :endDate")
  Integer countBidsByOperator(@Param("statusList") List<String> statusList, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.bidder.username = :username"
      + " AND b.createdAt > :startDate AND b.createdAt < :endDate")
  Integer countBids(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(b) FROM Bid b WHERE b.bidder.username = :username AND b.status IN :statusList"
      + " AND b.createdAt > :startDate AND b.createdAt < :endDate")
  Integer countBids(@Param("username") String username, @Param("statusList") List<String> statusList,
      @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

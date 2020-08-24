package com.crm.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.BiddingDocument;

@Repository
public interface BiddingDocumentRepository extends JpaRepository<BiddingDocument, Long> {

  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.username = :username")
  Page<BiddingDocument> findByMerchant(@Param("username") String username, Pageable pageable);

  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.username = :username AND bd.status = :status")
  Page<BiddingDocument> findByMerchant(@Param("username") String username, @Param("status") String status,
      Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE b.bidder.username = :username")
  Page<BiddingDocument> findByForwarder(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE b.bidder.username = :username AND bd.status = :status")
  Page<BiddingDocument> findByForwarder(@Param("username") String username, @Param("status") String status,
      Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE (bd.offeree.username = :username "
      + "OR b.bidder.username = :username) AND b.id = :id")
  Optional<BiddingDocument> findByBid(@Param("id") Long bid, @Param("username") String username);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b LEFT JOIN b.combined c WHERE (bd.offeree.username = :username "
      + "OR b.bidder.username = :username) AND c.id = :id")
  Optional<BiddingDocument> findByCombined(@Param("id") Long combined, @Param("username") String username);

  @Query(value = "SELECT DISTINCT bd FROM BiddingDocument bd LEFT JOIN bd.bids b "
      + "WHERE (bd.offeree.username = :username OR b.bidder.username = :username) AND b.combined.id IS NOT NULL")
  Page<BiddingDocument> findByExistCombined(@Param("username") String username, Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.outbound o LEFT JOIN o.booking bk"
      + " WHERE o.shippingLine.companyCode = :shippingLine"
      + " AND o.containerType.name = :containerType AND bd.status IN :status"
      + " AND o.packingTime > :emptyTime AND bk.cutOffTime < :freeTime")
  Page<BiddingDocument> findByInbound(@Param("shippingLine") String shippingLine,
      @Param("containerType") String containerType, @Param("status") List<String> status,
      @Param("emptyTime") LocalDateTime emptyTime, @Param("freeTime") LocalDateTime freeTime, Pageable pageable);

  @Query(value = "SELECT CASE WHEN COUNT(b) = 0 THEN TRUE ELSE FALSE END "
      + "FROM BiddingDocument bd JOIN bd.bids b LEFT JOIN b.bidder f" + " WHERE bd.id = :id AND f.username = :username")
  boolean isBidderByBiddingDocument(@Param("id") Long id, @Param("username") String username);

  @Query(value = "SELECT CASE WHEN COUNT(bd) > 0 THEN TRUE ELSE FALSE END FROM BiddingDocument bd JOIN bd.bids b"
      + " WHERE bd.id = :id AND b.status = 'ACCEPTED'")
  boolean existsCombinedBid(@Param("id") Long id);

  @Query(value = "SELECT COUNT(bd) FROM BiddingDocument bd"
      + " WHERE bd.createdAt > :startDate AND bd.createdAt < :endDate")
  Integer countBiddingDocumentsByOperator(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(bd) FROM BiddingDocument bd"
      + " WHERE bd.createdAt > :startDate AND bd.createdAt < :endDate AND bd.status IN :statusList")
  Integer countBiddingDocumentsByOperator(@Param("statusList") List<String> statusList, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(bd) FROM BiddingDocument bd WHERE bd.offeree.username = :username"
      + " AND bd.createdAt > :startDate AND bd.createdAt < :endDate")
  Integer countBiddingDocuments(@Param("username") String username, @Param("startDate") Date startDate,
      @Param("endDate") Date endDate);

  @Query(value = "SELECT COUNT(bd) FROM BiddingDocument bd"
      + " WHERE bd.offeree.username = :username AND bd.status IN :statusList"
      + " AND bd.createdAt > :startDate AND bd.createdAt < :endDate")
  Integer countBiddingDocuments(@Param("username") String username, @Param("status") String status,
      @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

package com.crm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Bid;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

  @Query(value = "SELECT b FROM Bid b WHERE b.biddingDocument.id = :id")
  Page<Bid> findByBiddingDocument(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b LEFT JOIN b.biddingDocument bd WHERE bd.id = :id "
      + "AND bd.offeree.id = :userId AND b.combined IS NOT NULL")
  Page<Bid> findByBiddingDocumentAndExistCombined(@Param("id") Long id, @Param("userId") Long userId, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.id = :id")
  Page<Bid> findByForwarder(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.id = :id AND b.status = :status")
  Page<Bid> findByForwarder(@Param("id") Long id, @Param("status") String status, Pageable pageable);

  @Query(value = "FROM Bid b WHERE b.biddingDocument.id = :id AND b.bidder.username = :username")
  Optional<Bid> findByBiddingDocumentAndForwarder(@Param("id") Long id, @Param("username") String username);

  @Query(value = "SELECT CASE WHEN COUNT(b) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Bid b JOIN b.containers c WHERE b.biddingDocument.id = :id AND c.status != 'DONE'")
  boolean isAllCombinedByBiddingDocument(@Param("id") Long id);

  @Query(value = "SELECT CASE WHEN COUNT(b) = 0 THEN TRUE ELSE FALSE END "
      + "FROM Bid b JOIN b.containers c WHERE b.biddingDocument.id = :id AND c.status != 'COMBINED'")
  boolean isAllAcceptedByBiddingDocument(@Param("id") Long id);
}

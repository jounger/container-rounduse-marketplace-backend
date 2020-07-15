package com.crm.repository;

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

  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.id = :id")
  Page<BiddingDocument> findByMerchant(@Param("id") Long merchantId, Pageable pageable);

  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.id = :id AND bd.status = :status")
  Page<BiddingDocument> findByMerchant(@Param("id") Long merchantId, @Param("status") String status, Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE b.bidder.id = :id")
  Page<BiddingDocument> findByForwarder(@Param("id") Long forwarder, Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE b.bidder.id = :id AND bd.status = :status")
  Page<BiddingDocument> findByForwarder(@Param("id") Long forwarderId, @Param("status") String status,
      Pageable pageable);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b WHERE (bd.offeree.username = :username "
      + "OR b.bidder.username = :username) AND b.id = :id")
  Optional<BiddingDocument> findByBid(@Param("id") Long bid, @Param("username") String username);

  @Query(value = "SELECT bd FROM BiddingDocument bd LEFT JOIN bd.bids b "
      + "WHERE (bd.offeree.id = :id OR b.bidder.id = :id) AND b.combined IS NOT NULL")
  Page<BiddingDocument> findByExistCombined(@Param("id") Long userId, Pageable pageable);
}

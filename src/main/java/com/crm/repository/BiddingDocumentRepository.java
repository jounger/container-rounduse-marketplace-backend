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
}

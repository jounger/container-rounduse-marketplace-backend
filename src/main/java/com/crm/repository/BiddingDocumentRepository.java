package com.crm.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.BiddingDocument;

public interface BiddingDocumentRepository extends JpaRepository<BiddingDocument, Long>{

  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.id = :id")
  Page<BiddingDocument> findBiddingDocumentByMerchant(@Param("id") Long merchantId, Pageable pageable);
  
  @Query(value = "SELECT bd FROM BiddingDocument bd JOIN bd.bids b WHERE b.bidder.id = :id")
  Page<BiddingDocument> findBiddingDocumentByForwarder(@Param("id") Long forwarder, Pageable pageable);
  
  @Query(value = "FROM BiddingDocument bd WHERE bd.offeree.id = :id AND bd.status = :status")
  Page<BiddingDocument> findBiddingDocumentByMerchant(@Param("id") Long merchantId, @Param("status") String status, Pageable pageable);
  
  @Query(value = "SELECT bd FROM BiddingDocument bd JOIN bd.bids b WHERE b.bidder.id = :id AND bd.status = :status")
  Page<BiddingDocument> findBiddingDocumentByForwarder(@Param("id") Long forwarderId, @Param("status") String status, Pageable pageable);
  
}

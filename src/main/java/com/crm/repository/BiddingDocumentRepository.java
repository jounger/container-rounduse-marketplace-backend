package com.crm.repository;

import java.awt.print.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.BiddingDocument;

public interface BiddingDocumentRepository extends JpaRepository<BiddingDocument, Long>{

  @Query(value = "FROM BiddingDocument bd WHERE bd.Merchant.id = :id")
  Page<BiddingDocument> findBiddingDocumentByMerchant(@Param("id") Long merchantId, Pageable pageable);
}

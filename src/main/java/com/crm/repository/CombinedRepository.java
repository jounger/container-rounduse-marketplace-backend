package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crm.models.Combined;

public interface CombinedRepository extends JpaRepository<Combined, Long>{

  @Query(value = "FROM Combined c WHERE c.biddingDocument.offeree.id = :id")
  Page<Combined> findByMerchant(@Param("id") Long id, Pageable pageable);
  
  @Query(value = "SELECT c FROM Combined c JOIN c.biddingDocument bd JOIN bd.bids b WHERE b.bidder.id = :id")
  Page<Combined> findByForwarder(@Param("id") Long id, Pageable pageable);
}

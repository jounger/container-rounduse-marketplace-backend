package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Combined;

@Repository
public interface CombinedRepository extends JpaRepository<Combined, Long> {

  @Query(value = "FROM Combined c WHERE c.bid.biddingDocument.offeree.id = :id")
  Page<Combined> findByMerchant(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Combined c JOIN c.bid b WHERE b.bidder.id = :id")
  Page<Combined> findByForwarder(@Param("id") Long id, Pageable pageable);

  @Query(value = "SELECT c FROM Combined c LEFT JOIN c.bid b LEFT JOIN b.biddingDocument bd "
      + "WHERE bd.id =:id AND (bd.offeree.id = :userId OR b.bidder.id = :userId)")
  Page<Combined> findByBiddingDocument(@Param("id") Long id, @Param("userId") Long userId, Pageable pageable);

}

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
public interface BidRepository extends JpaRepository<Bid, Long>{

  @Query(value = "SELECT b FROM Bid b WHERE b.biddingDocument.id = :id")
  Page<Bid> findBidsByBiddingDocument(@Param("id") Long id, Pageable pageable);
  
  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.id = :id")
  Page<Bid> findBidsByForwarder(@Param("id") Long id, Pageable pageable);
  
  @Query(value = "FROM Bid b WHERE b.biddingDocument.id = :id AND b.bidder.username = :username")
  Optional<Bid> findBidByBiddingDocumentAndForwarder(@Param("id") Long id, @Param("username") String username);
}

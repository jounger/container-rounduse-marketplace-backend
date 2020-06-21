package com.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crm.models.Bid;
import com.crm.payload.request.PaginationRequest;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long>{

  @Query(value = "SELECT b FROM Bid b WHERE b.biddingDocument.id = :id")
  Page<Bid> getBidsByBiddingDocument(@Param("id") Long id, PaginationRequest request);
  
  @Query(value = "SELECT b FROM Bid b WHERE b.bidder.id = :id")
  Page<Bid> getBidsByForwarder(@Param("id") Long id, PaginationRequest request);
}

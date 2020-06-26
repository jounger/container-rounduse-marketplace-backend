package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;

public interface BidService {
  
  void createBid(BidRequest request);
  
  Bid getBid(Long id);
  
  Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request);
  
  Page<Bid> getBidsByForwarder(Long id, PaginationRequest request);
  
  //update full biddingDocument
  Bid updateBid(BidRequest request);
  
  //update part biddingDocument
  Bid editBid(Long id, Map<String, Object> updates);
  
  void removeBid(Long id);
  
}

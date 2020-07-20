package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;

public interface BidService {

  Bid createBid(Long bidDocId, Long id, BidRequest request);

  Bid getBid(Long id);

  Bid getBidByBiddingDocumentAndForwarder(Long biddingDocument, String username);

  Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request);
  
  Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, Long userId, PaginationRequest request);

  Page<Bid> getBidsByForwarder(Long id, PaginationRequest request);

  // update full biddingDocument
  Bid updateBid(String username, BidRequest request);

  // update part biddingDocument
  Bid editBid(Long id, String username, Map<String, Object> updates);

  void removeBid(Long id);

}

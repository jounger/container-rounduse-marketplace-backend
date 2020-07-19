package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;

public interface BidService {

  Bid createBid(Long bidDocId, Long id, BidRequest request);

  Bid getBid(Long id, Long userId);

  Bid getBidByBiddingDocumentAndForwarder(Long biddingDocument, Long userId);

  Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request);
  
  Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, Long userId, PaginationRequest request);

  Page<Bid> getBidsByForwarder(Long id, PaginationRequest request);

  // update full biddingDocument
  Bid updateBid(Long userId, BidRequest request);

  // update part biddingDocument
  Bid editBid(Long id, Long userId, Map<String, Object> updates);

  void removeBid(Long id, Long userId);

}

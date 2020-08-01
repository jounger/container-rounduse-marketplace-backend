package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;

public interface BidService {

  Bid createBid(Long bidDocId, String username, BidRequest request);

  Bid getBid(Long id, String username);

  Bid getBidByBiddingDocumentAndForwarder(Long biddingDocument, String username);

  Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request);

  Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, String username, PaginationRequest request);

  Page<Bid> getBidsByForwarder(String username, PaginationRequest request);

  Bid replaceContainer(Long id, String username, Map<String, String> updates);

  Bid addContainer(Long id, String username, Long containerId);

  Bid removeContainer(Long id, String username, Long containerId);

  // update part biddingDocument
  Bid editBid(Long id, String username, Map<String, Object> updates);

  void removeBid(Long id, String username);

}

package com.crm.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Bid;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReplaceContainerRequest;

public interface BidService {

  Bid createBid(Long bidDocId, String username, BidRequest request);

  Bid getBid(Long id, String username);

  Page<Bid> getBidsByBiddingDocument(Long id, String username, PaginationRequest request);

  Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, String username, PaginationRequest request);

  Page<Bid> getBidsByForwarder(String username, PaginationRequest request);

  Bid replaceContainer(Long id, String username, ReplaceContainerRequest request);

  Bid addContainer(Long id, String username, BidRequest request);

  Bid removeContainer(Long id, String username, Long containerId);

  // update part biddingDocument
  Bid editBid(Long id, String username, Map<String, Object> updates);

  Bid editBidWhenCombined(Long id, String username, List<Long> containersId);

  void removeBid(Long id, String username);

  void editExpiredBids(Bid bid, String status);

  List<Bid> updateExpiredBidFromList(List<Bid> bids);

}

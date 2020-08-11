package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BiddingDocument;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;

public interface BiddingDocumentService {

  BiddingDocument createBiddingDocument(String username, BiddingDocumentRequest request);

  BiddingDocument getBiddingDocument(Long id);

  BiddingDocument getBiddingDocumentByBid(Long id, String username);
  
  BiddingDocument getBiddingDocumentByCombined(Long id, String username);

  Page<BiddingDocument> getBiddingDocumentsByExistCombined(String username, PaginationRequest request);

  Page<BiddingDocument> getBiddingDocuments(String username, PaginationRequest request);

  // update full biddingDocument
  BiddingDocument updateBiddingDocument(BiddingDocumentRequest request);

  // update part biddingDocument
  BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates);

  void removeBiddingDocument(Long id, String username);

}

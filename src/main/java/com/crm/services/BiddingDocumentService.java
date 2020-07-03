package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BiddingDocument;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;

public interface BiddingDocumentService {
  
  BiddingDocument createBiddingDocument(Long id, BiddingDocumentRequest request);
  
  BiddingDocument getBiddingDocument(Long id);
  
  Page<BiddingDocument> getBiddingDocuments(PaginationRequest request);
  
  Page<BiddingDocument> getBiddingDocumentsByMerchant(Long id, PaginationRequest request);
  
  Page<BiddingDocument> getBiddingDocumentsByForwarder(Long id, PaginationRequest request);
  
  //update full biddingDocument
  BiddingDocument updateBiddingDocument(BiddingDocumentRequest request);
  
  //update part biddingDocument
  BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates);
  
  void removeBiddingDocument(Long id);
  
}

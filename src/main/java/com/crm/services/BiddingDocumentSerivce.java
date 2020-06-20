package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.BiddingDocument;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;

public interface BiddingDocumentSerivce {
  
  void saveBiddingDocument(BiddingDocumentRequest request);
  
  BiddingDocument findBiddingDocument(Long id);
  
  Page<BiddingDocument> findBiddingDocuments(PaginationRequest request);
  
  Page<BiddingDocument> findBiddingDocumentsByMerchant(Long id, PaginationRequest request);
  
  void deleteBiddingDocument(Long id);
  
  //update full biddingDocument
  BiddingDocument updateBiddingDocument(BiddingDocumentRequest request);
  
  //update part biddingDocument
  BiddingDocument editBiddingDocument(Long id, Map<String, Object> updates);
}

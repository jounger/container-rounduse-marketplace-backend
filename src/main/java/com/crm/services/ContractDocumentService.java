package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContractDocument;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContractDocumentService {

  ContractDocument createContractDocument(Long id, String username, ContractDocumentRequest request);

  Page<ContractDocument> getContractDocumentsByUser(String username, PaginationRequest request);
  
  Page<ContractDocument> getContractDocumentsByContract(Long id, String username, PaginationRequest request);

  Page<ContractDocument> searchContractDocuments(PaginationRequest request, String search);

  ContractDocument editContractDocument(Long id, String username, Map<String, Object> updates);

  void removeContractDocument(Long id, String username);
}

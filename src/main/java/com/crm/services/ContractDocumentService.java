package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.ContractDocument;
import com.crm.payload.request.ContractDocumentRequest;
import com.crm.payload.request.PaginationRequest;

public interface ContractDocumentService {

  ContractDocument createEvidence(Long id, String username, ContractDocumentRequest request);

  Page<ContractDocument> getEvidencesByUser(String username, PaginationRequest request);
  
  Page<ContractDocument> getEvidencesByContract(Long id, String username, PaginationRequest request);

  Page<ContractDocument> searchEvidences(PaginationRequest request, String search);

  ContractDocument editEvidence(Long id, String username, Map<String, Object> updates);

  void removeEvidence(Long id, String username);
}

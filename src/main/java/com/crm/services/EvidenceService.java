package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Evidence;
import com.crm.payload.request.EvidenceRequest;
import com.crm.payload.request.PaginationRequest;

public interface EvidenceService {

  Evidence createEvidence(Long id, Long userId, EvidenceRequest request);

  Page<Evidence> getEvidencesByUser(Long userId, PaginationRequest request);
  
  Page<Evidence> getEvidencesByContract(Long id, Long userId, PaginationRequest request);

  Page<Evidence> searchEvidences(PaginationRequest request, String search);

  Evidence editEvidence(Long id, Long userId, Map<String, Object> updates);

  void removeEvidence(Long id, Long userId);
}

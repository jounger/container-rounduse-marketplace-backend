package com.crm.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.crm.models.Evidence;
import com.crm.payload.request.EvidenceRequest;
import com.crm.payload.request.PaginationRequest;

public interface EvidenceService {

  Evidence createEvidence(Long id, String username, EvidenceRequest request);

  Page<Evidence> getEvidencesByUser(String username, PaginationRequest request);

  Page<Evidence> searchEvidences(PaginationRequest request, String search);

  Evidence editEvidence(Long id, String username, Map<String, Object> updates);

  void removeEvidence(Long id, String username);
}

package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Consignment;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;

public interface ConsignmentService {
  
  Consignment getConsignmentById(Long id);
  
  Page<Consignment> getConsignments(PaginationRequest request);
  
  Page<Consignment> getConsignmentsByMerchant(Long id, PaginationRequest request);
  
  void createConsignment(ConsignmentRequest request);
  
  Consignment updateConsignment(ConsignmentRequest request);
  
  void removeConsignment(Long id);
  
}

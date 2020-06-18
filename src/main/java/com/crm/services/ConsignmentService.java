package com.crm.services;

import org.springframework.data.domain.Page;

import com.crm.models.Consignment;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;

public interface ConsignmentService {
  
  Page<Consignment> getListConsignment(PaginationRequest request);
  
  void saveConsignment(ConsignmentRequest request);
  
  void editConsignment(ConsignmentRequest request);
  
  void deleteConsignment(Long id);
  
  Consignment findConsignmentById(Long id);
  
}

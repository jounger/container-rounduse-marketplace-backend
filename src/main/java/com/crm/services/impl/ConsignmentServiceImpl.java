package com.crm.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.crm.models.Consignment;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.ConsignmentRepository;
import com.crm.services.ConsignmentService;

@Service
public class ConsignmentServiceImpl implements ConsignmentService{
  
  private ConsignmentRepository consignmentRepository;

  @Override
  public Page<Consignment> getListConsignment(PaginationRequest request) {
    Page<Consignment> pages = consignmentRepository.findAll(PageRequest.of(request.getPage(), request.getLimit()));
    return pages;
  }

  @Override
  public void saveConsignment(ConsignmentRequest request) {
    // TODO Auto-generated method stub
    
  }}

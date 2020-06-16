package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Consignment;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ConsignmentService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/consignment")
public class ConsignmentController {
  
  private ConsignmentService consignmentService;
  
  public ResponseEntity<?> getListConsignment(@Valid PaginationRequest request) {
    
    Page<Consignment> pages = consignmentService.getListConsignment(request);
    PaginationResponse<Consignment> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    response.setContents(pages.getContent());
    return ResponseEntity.ok(response);
    
  }
}

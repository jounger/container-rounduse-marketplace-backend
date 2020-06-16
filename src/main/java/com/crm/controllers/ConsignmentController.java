package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Consignment;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ConsignmentService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/consignment")
public class ConsignmentController {
  
  private ConsignmentService consignmentService;
  
  @GetMapping("/")
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
  
  @PostMapping("/")
  public ResponseEntity<?> createConsignment(@Valid @RequestBody ConsignmentRequest request){       
    consignmentService.saveConsignment(request);
    return ResponseEntity.ok(new MessageResponse("Consignment created successfully"));
  }
  
  @DeleteMapping("/{consignmentId}")
  public ResponseEntity<?> removeConsignment(@PathVariable Long consignmentId){       
    consignmentService.deleteConsignment(consignmentId);
    return ResponseEntity.ok(new MessageResponse("Consignment has remove successfully"));
  }
  
  @PutMapping("/{consignmentId}")
  public ResponseEntity<?> editConsignment(@PathVariable Long consignmentId, @Valid @RequestBody ConsignmentRequest request){
    consignmentService.editConsignment(consignmentId, request);
    return ResponseEntity.ok(new MessageResponse("Consignment has update successfully"));
  }
  
  @GetMapping("/{consignmentId}")
  public ResponseEntity<?> getConsignment(@PathVariable Long consignmentId){
    Consignment consignment = consignmentService.findConsignmentById(consignmentId);
    return ResponseEntity.ok(consignment);
  }
}

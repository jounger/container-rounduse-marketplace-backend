package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import com.crm.models.dto.ConsignmentDto;
import com.crm.models.mapper.ConsignmentMapper;
import com.crm.payload.request.ConsignmentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ConsignmentService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/consignment")
public class ConsignmentController {
  
  private static final Logger logger = LoggerFactory.getLogger(ConsignmentController.class);
  
  @Autowired
  private ConsignmentService consignmentService;
  
  @GetMapping("")
  public ResponseEntity<?> getListConsignment(@Valid @RequestBody PaginationRequest request) {
    
    Page<Consignment> pages = consignmentService.getListConsignment(request);
    PaginationResponse<ConsignmentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    
    List<Consignment> consignments = pages.getContent();
    List<ConsignmentDto> consignmentsDto = new ArrayList<>();
    consignments.forEach(consignment -> consignmentsDto.add(ConsignmentMapper.toConsignmentDto(consignment)));
    response.setContents(consignmentsDto);
    
    return ResponseEntity.ok(response);
    
  }
  
  @PostMapping("")
  public ResponseEntity<?> createConsignment(@Valid @RequestBody ConsignmentRequest request){
    logger.error("Runtime error: {}", request);
    consignmentService.saveConsignment(request);
    return ResponseEntity.ok(new MessageResponse("Consignment created successfully"));
  }
  
  @Transactional
  @DeleteMapping("")
  public ResponseEntity<?> removeConsignment(@Valid @RequestBody ConsignmentRequest request){       
    consignmentService.deleteConsignment(request.getId());
    return ResponseEntity.ok(new MessageResponse("Consignment has remove successfully"));
  }
  
  @Transactional
  @PutMapping("")
  public ResponseEntity<?> editConsignment(@Valid @RequestBody ConsignmentRequest request){
    consignmentService.editConsignment(request);
    return ResponseEntity.ok(new MessageResponse("Consignment has update successfully"));
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<?> getConsignment(@PathVariable Long id){
    Consignment consignment = consignmentService.findConsignmentById(id);
    ConsignmentDto consignmentDto = new ConsignmentDto();
    consignmentDto = ConsignmentMapper.toConsignmentDto(consignment);
    return ResponseEntity.ok(consignmentDto);
  }
}

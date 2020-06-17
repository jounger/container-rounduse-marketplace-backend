package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Container;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/container")
public class ContainerController {
  
  @Autowired
  private ContainerService containerService;
  
  @GetMapping("/")
  public ResponseEntity<?> getContainers(@Valid @RequestBody PaginationRequest request) {
    Page<Container> pages = containerService.getContainers(request);
    PaginationResponse<Container> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    response.setContents(pages.getContent());
    
    return ResponseEntity.ok(response);
  }
  
  @PostMapping("/")
  public ResponseEntity<?> createRole(@Valid @RequestBody ContainerRequest request) {
    containerService.saveContainer(request);
    return ResponseEntity.ok(new MessageResponse("Container has been created successfully"));
  }
  
}

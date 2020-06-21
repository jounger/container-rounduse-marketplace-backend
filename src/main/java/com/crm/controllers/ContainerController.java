package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;
import com.crm.models.mapper.ContainerMapper;
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
  
  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getContainers(@Valid PaginationRequest request) {
    Page<Container> pages = containerService.getContainers(request);
    PaginationResponse<ContainerDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    
    List<Container> containers = pages.getContent();
    List<ContainerDto> containerDto = new ArrayList<>();
    containers.forEach(container -> containerDto.add(ContainerMapper.toContainerDto(container)));
    response.setContents(containerDto);
    
    return ResponseEntity.ok(response);
  }
  
  @GetMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('FORWARDER')")
  public ResponseEntity<?> getContainer(@PathVariable Long id){
    Container container = containerService.getContainerById(id);
    ContainerDto containerDto = new ContainerDto();
    containerDto = ContainerMapper.toContainerDto(container);
    return ResponseEntity.ok(containerDto);
  }
  
  @GetMapping("/forwarder/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getContainersByForwarder(@PathVariable Long id, @Valid PaginationRequest request) {
    
    Page<Container> pages = containerService.getContainersByMerchant(id, request);
    PaginationResponse<ContainerDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());
    
    List<Container> containers = pages.getContent();
    List<ContainerDto> containerDto = new ArrayList<>();
    containers.forEach(container -> containerDto.add(ContainerMapper.toContainerDto(container)));
    response.setContents(containerDto);
    
    return ResponseEntity.ok(response);
    
  }
  
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainer(@Valid @RequestBody ContainerRequest request) {
    containerService.saveContainer(request);
    return ResponseEntity.ok(new MessageResponse("Container has been created successfully"));
  }
  
  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> updateContainer(@Valid @RequestBody ContainerRequest request){
    containerService.updateContainer(request);
    return ResponseEntity.ok(new MessageResponse("Container has update successfully"));
  }
  
  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainer(@PathVariable Long id){       
    containerService.removeContainer(id);
    return ResponseEntity.ok(new MessageResponse("Container has remove successfully"));
  }
    
}

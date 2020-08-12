package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Container;
import com.crm.models.dto.ContainerDto;
import com.crm.models.mapper.ContainerMapper;
import com.crm.payload.request.ContainerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container")
public class ContainerController {

  @Autowired
  private ContainerService containerService;

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
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
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainer(@PathVariable Long id) {
    Container container = containerService.getContainerById(id);
    ContainerDto containerDto = new ContainerDto();
    containerDto = ContainerMapper.toContainerDto(container);
    return ResponseEntity.ok(containerDto);
  }

  @GetMapping("/bill-of-lading/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainersByBillOfLading(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Container> pages = containerService.getContainersByBillOfLading(id, request);
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

  @GetMapping("/inbound/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainersByInbound(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Container> pages = containerService.getContainersByInbound(id, request);
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

  @GetMapping("/bid/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainersByBid(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Container> pages = containerService.getContainersByBid(id, request);
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

  @Transactional
  @PostMapping("/bill-of-lading/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainer(@PathVariable Long id, @Valid @RequestBody ContainerRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Container container = containerService.createContainer(id, username, request);
    ContainerDto containerDto = ContainerMapper.toContainerDto(container);

    // Set default response body
    DefaultResponse<ContainerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_CONTAINER_SUCCESSFULLY);
    defaultResponse.setData(containerDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainer(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Container container = containerService.editContainer(updates, id, username);
    ContainerDto containerDto = new ContainerDto();
    containerDto = ContainerMapper.toContainerDto(container);
    
    // Set default response body
    DefaultResponse<ContainerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_CONTAINER_SUCCESSFULLY);
    defaultResponse.setData(containerDto);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainer(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    containerService.removeContainer(id, username);
    
    // Set default response body
    DefaultResponse<ContainerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_CONTAINER_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(defaultResponse);
  }

}

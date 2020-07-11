package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.ContainerTractor;
import com.crm.models.dto.ContainerTractorDto;
import com.crm.models.mapper.ContainerTractorMapper;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.ContainerTractorService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-tractor")
public class ContainerTractorController {

  @Autowired
  ContainerTractorService containerTractorService;

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainerTractors(@Valid PaginationRequest request) {
    Page<ContainerTractor> pages = containerTractorService.getContainerTractors(request);

    PaginationResponse<ContainerTractorDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerTractor> containerTractors = pages.getContent();
    List<ContainerTractorDto> containerTractorDto = new ArrayList<>();
    containerTractors.forEach(
        containerTractor -> containerTractorDto.add(ContainerTractorMapper.toContainerTractorDto(containerTractor)));
    response.setContents(containerTractorDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/forwarder")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainerTractorsByForwarder(@Valid PaginationRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<ContainerTractor> pages = containerTractorService.getContainerTractorsByForwarder(userId, request);

    PaginationResponse<ContainerTractorDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerTractor> containerTractors = pages.getContent();
    List<ContainerTractorDto> containerTractorDto = new ArrayList<>();
    containerTractors.forEach(
        containerTractor -> containerTractorDto.add(ContainerTractorMapper.toContainerTractorDto(containerTractor)));
    response.setContents(containerTractorDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainerTractor(@PathVariable Long id) {
    ContainerTractor containerTractor = containerTractorService.getContainerTractorById(id);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);
    return ResponseEntity.ok(containerTractorDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainerTractor(@Valid @RequestBody ContainerTractorRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerTractor containerTractor = containerTractorService.createContainerTractor(userId, request);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);
    return ResponseEntity.ok(containerTractorDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> updateContainerTractor(@Valid @RequestBody ContainerTractorRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerTractor containerTractor = containerTractorService.updateContainerTractor(userId, request);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);
    return ResponseEntity.ok(containerTractorDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerTractor(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerTractor containerTractor = containerTractorService.editContainerTractor(updates, id, userId);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);
    return ResponseEntity.ok(containerTractorDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainerTractor(@PathVariable Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    containerTractorService.removeContainerTractor(id, userId);
    return ResponseEntity.ok(new MessageResponse("ContainerTractor has remove successfully"));
  }
}

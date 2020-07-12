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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.ContainerSemiTrailer;
import com.crm.models.dto.ContainerSemiTrailerDto;
import com.crm.models.mapper.ContainerSemiTrailerMapper;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.ContainerSemiTrailerService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-semi-trailer")
public class ContainerSemiTrailerController {

  @Autowired
  ContainerSemiTrailerService containerSemiTrailerService;

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainerSemiTrailers(@Valid PaginationRequest request) {
    Page<ContainerSemiTrailer> pages = containerSemiTrailerService.getContainerSemiTrailers(request);

    PaginationResponse<ContainerSemiTrailerDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerSemiTrailer> containerSemiTrailers = pages.getContent();
    List<ContainerSemiTrailerDto> containerSemiTrailerDto = new ArrayList<>();
    containerSemiTrailers.forEach(containerSemiTrailer -> containerSemiTrailerDto
        .add(ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer)));
    response.setContents(containerSemiTrailerDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/filter")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> searchContainerSemiTrailers(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<ContainerSemiTrailer> pages = containerSemiTrailerService.searchContainerSemiTrailers(request, search);

    PaginationResponse<ContainerSemiTrailerDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerSemiTrailer> containerSemiTrailers = pages.getContent();
    List<ContainerSemiTrailerDto> containerSemiTrailerDto = new ArrayList<>();
    containerSemiTrailers.forEach(containerSemiTrailer -> containerSemiTrailerDto
        .add(ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer)));
    response.setContents(containerSemiTrailerDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/forwarder")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getContainerSemiTrailersByForwarder(@Valid PaginationRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Page<ContainerSemiTrailer> pages = containerSemiTrailerService.getContainerSemiTrailersByForwarder(userId, request);

    PaginationResponse<ContainerSemiTrailerDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerSemiTrailer> containerSemiTrailers = pages.getContent();
    List<ContainerSemiTrailerDto> containerSemiTrailerDto = new ArrayList<>();
    containerSemiTrailers.forEach(containerSemiTrailer -> containerSemiTrailerDto
        .add(ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer)));
    response.setContents(containerSemiTrailerDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getContainerSemiTrailer(@PathVariable Long id) {
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.getContainerSemiTrailerById(id);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);
    return ResponseEntity.ok(containerSemiTrailerDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainerSemiTrailer(@Valid @RequestBody ContainerSemiTrailerRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.createContainerSemiTrailer(userId, request);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);
    return ResponseEntity.ok(containerSemiTrailerDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> updateContainerSemiTrailer(@Valid @RequestBody ContainerSemiTrailerRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.updateContainerSemiTrailer(userId, request);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);
    return ResponseEntity.ok(containerSemiTrailerDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerSemiTrailer(@RequestBody Map<String, Object> updates,
      @PathVariable("id") Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.editContainerSemiTrailer(updates, id,
        userId);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);
    return ResponseEntity.ok(containerSemiTrailerDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainerSemiTrailer(@PathVariable Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    containerSemiTrailerService.removeContainerSemiTrailer(id, userId);
    return ResponseEntity.ok(new MessageResponse("ContainerSemiTrailer has remove successfully"));
  }

}

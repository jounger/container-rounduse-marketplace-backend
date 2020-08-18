package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.ContainerSemiTrailer;
import com.crm.models.dto.ContainerSemiTrailerDto;
import com.crm.models.mapper.ContainerSemiTrailerMapper;
import com.crm.payload.request.ContainerSemiTrailerRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerSemiTrailerService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-semi-trailer")
public class ContainerSemiTrailerController {

  private static final Logger logger = LoggerFactory.getLogger(ContainerSemiTrailerController.class);

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

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ContainerSemiTrailer> pages = containerSemiTrailerService.getContainerSemiTrailersByForwarder(username,
        request);

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

  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  @RequestMapping(method = RequestMethod.GET, params = { "licensePlate" })
  public ResponseEntity<?> getContainerSemiTrailerByLicensePlate(@RequestParam String licensePlate) {
    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService
        .getContainerSemiTrailerByLicensePlate(licensePlate);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);
    return ResponseEntity.ok(containerSemiTrailerDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainerSemiTrailer(@Valid @RequestBody ContainerSemiTrailerRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.createContainerSemiTrailer(username,
        request);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);

    // Set default response body
    DefaultResponse<ContainerSemiTrailerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_CONTAINER_SEMI_TRAILER_SUCCESSFULLY);
    defaultResponse.setData(containerSemiTrailerDto);

    logger.info("User {} createContainerSemiTrailer with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerSemiTrailer(@RequestBody Map<String, Object> updates,
      @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    ContainerSemiTrailer containerSemiTrailer = containerSemiTrailerService.editContainerSemiTrailer(updates, id,
        username);
    ContainerSemiTrailerDto containerSemiTrailerDto = new ContainerSemiTrailerDto();
    containerSemiTrailerDto = ContainerSemiTrailerMapper.toContainerSemiTrailerDto(containerSemiTrailer);

    // Set default response body
    DefaultResponse<ContainerSemiTrailerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_CONTAINER_SEMI_TRAILER_SUCCESSFULLY);
    defaultResponse.setData(containerSemiTrailerDto);

    logger.info("User {} editContainerSemiTrailer from trailer id {} with request: {}", username, id,
        updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainerSemiTrailer(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    containerSemiTrailerService.removeContainerSemiTrailer(id, username);

    // Set default response body
    DefaultResponse<ContainerSemiTrailerDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_CONTAINER_SEMI_TRAILER_SUCCESSFULLY);

    logger.info("User {} deleteContainerSemiTrailer with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}

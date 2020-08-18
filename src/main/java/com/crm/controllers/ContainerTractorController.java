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
import com.crm.models.ContainerTractor;
import com.crm.models.dto.ContainerTractorDto;
import com.crm.models.mapper.ContainerTractorMapper;
import com.crm.payload.request.ContainerTractorRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerTractorService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-tractor")
public class ContainerTractorController {

  private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

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

  @GetMapping("/filter")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> searchContainerTractors(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {
    Page<ContainerTractor> pages = containerTractorService.searchContainerTractors(request, search);

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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<ContainerTractor> pages = containerTractorService.getContainerTractorsByForwarder(username, request);

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

  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  @RequestMapping(method = RequestMethod.GET, params = { "licensePlate" })
  public ResponseEntity<?> getContainerTractorByLicensePlate(@RequestParam String licensePlate) {
    ContainerTractor containerTractor = containerTractorService.getContainerTractorByLicensePlate(licensePlate);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);
    return ResponseEntity.ok(containerTractorDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createContainerTractor(@Valid @RequestBody ContainerTractorRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    ContainerTractor containerTractor = containerTractorService.createContainerTractor(username, request);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);

    // Set default response body
    DefaultResponse<ContainerTractorDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_CONTAINER_TRACTOR_SUCCESSFULLY);
    defaultResponse.setData(containerTractorDto);

    logger.info("User {} createContainerTractor with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerTractor(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    ContainerTractor containerTractor = containerTractorService.editContainerTractor(updates, id, username);
    ContainerTractorDto containerTractorDto = new ContainerTractorDto();
    containerTractorDto = ContainerTractorMapper.toContainerTractorDto(containerTractor);

    // Set default response body
    DefaultResponse<ContainerTractorDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_CONTAINER_TRACTOR_SUCCESSFULLY);
    defaultResponse.setData(containerTractorDto);

    logger.info("User {} editContainerTractor from tractor id {} with request: {}", username, id,
        updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeContainerTractor(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    containerTractorService.removeContainerTractor(id, username);

    // Set default response body
    DefaultResponse<ContainerTractorDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_CONTAINER_TRACTOR_SUCCESSFULLY);

    logger.info("User {} deleteContainerTractor with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}

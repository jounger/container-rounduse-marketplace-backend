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
import com.crm.models.ContainerType;
import com.crm.models.dto.ContainerTractorDto;
import com.crm.models.dto.ContainerTypeDto;
import com.crm.models.mapper.ContainerTypeMapper;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerTypeService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-type")
public class ContainerTypeController {

  private static final Logger logger = LoggerFactory.getLogger(ContainerTypeController.class);

  @Autowired
  private ContainerTypeService containerTypeService;

  @GetMapping("")
  public ResponseEntity<?> getContainerTypes(@Valid PaginationRequest request) {

    Page<ContainerType> pages = containerTypeService.getContainerTypes(request);
    PaginationResponse<ContainerTypeDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerType> containerTypes = pages.getContent();
    List<ContainerTypeDto> containerTypeDto = new ArrayList<>();
    containerTypes
        .forEach(containerType -> containerTypeDto.add(ContainerTypeMapper.toContainerTypeDto(containerType)));
    response.setContents(containerTypeDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/filter")
  public ResponseEntity<?> searchContainerTypes(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<ContainerType> pages = containerTypeService.searchContainerTypes(request, search);
    PaginationResponse<ContainerTypeDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<ContainerType> containerTypes = pages.getContent();
    List<ContainerTypeDto> containerTypeDto = new ArrayList<>();
    containerTypes
        .forEach(containerType -> containerTypeDto.add(ContainerTypeMapper.toContainerTypeDto(containerType)));
    response.setContents(containerTypeDto);

    return ResponseEntity.ok(response);

  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getContainerType(@PathVariable Long id) {
    ContainerType containerType = containerTypeService.getContainerTypeById(id);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @RequestMapping(method = RequestMethod.GET, params = { "name" })
  public ResponseEntity<?> getContainerTypeByName(@RequestParam String name) {
    ContainerType containerType = containerTypeService.getContainerTypeByName(name);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createContainerType(@Valid @RequestBody ContainerTypeRequest request) {
    ContainerType containerType = containerTypeService.createContainerType(request);
    ContainerTypeDto containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);

    // Set default response body
    DefaultResponse<ContainerTypeDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_CONTAINER_TYPE_SUCCESSFULLY);
    defaultResponse.setData(containerTypeDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerType(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    ContainerType containerType = containerTypeService.editContainerType(updates, id);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);

    // Set default response body
    DefaultResponse<ContainerTypeDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_CONTAINER_TYPE_SUCCESSFULLY);
    defaultResponse.setData(containerTypeDto);

    logger.info("editContainerType from id {} with request: {}", id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeContainerType(@PathVariable Long id) {
    containerTypeService.removeContainerType(id);

    // Set default response body
    DefaultResponse<ContainerTractorDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_CONTAINER_TYPE_SUCCESSFULLY);

    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}

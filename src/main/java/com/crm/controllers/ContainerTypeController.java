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

import com.crm.models.ContainerType;
import com.crm.models.dto.ContainerTypeDto;
import com.crm.models.mapper.ContainerTypeMapper;
import com.crm.payload.request.ContainerTypeRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ContainerTypeService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/container-type")
public class ContainerTypeController {

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

  @GetMapping("/{id}")
  public ResponseEntity<?> getContainerType(@PathVariable Long id) {
    ContainerType containerType = containerTypeService.getContainerTypeById(id);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @RequestMapping(method = RequestMethod.GET, params = {"name"})
  public ResponseEntity<?> getContainerTypeByName(@RequestParam String name) {
    ContainerType containerType = containerTypeService.getContainerTypeByName(name);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @PostMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> createContainerType(@Valid @RequestBody ContainerTypeRequest request) {
    ContainerType containerType = containerTypeService.createContainerType(request);
    ContainerTypeDto containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> updateContainerType(@Valid @RequestBody ContainerTypeRequest request) {
    ContainerType containerType = containerTypeService.updateContainerType(request);
    ContainerTypeDto containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @PreAuthorize("hasRole('MODERATOR')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editContainerType(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    ContainerType containerType = containerTypeService.editContainerType(updates, id);
    ContainerTypeDto containerTypeDto = new ContainerTypeDto();
    containerTypeDto = ContainerTypeMapper.toContainerTypeDto(containerType);
    return ResponseEntity.ok(containerTypeDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> removeContainerType(@PathVariable Long id) {
    containerTypeService.removeContainerType(id);
    return ResponseEntity.ok(new MessageResponse("Container Type has remove successfully"));
  }
}

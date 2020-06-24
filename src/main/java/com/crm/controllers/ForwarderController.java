package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.crm.models.Forwarder;
import com.crm.models.dto.ForwarderDto;
import com.crm.models.mapper.ForwarderMapper;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ForwarderRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.ForwarderService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/forwarder")
public class ForwarderController {
  @Autowired
  private ForwarderService forwarderService;

  @PostMapping("")
  public ResponseEntity<?> createForwarder(@Valid @RequestBody ForwarderRequest request) {
    forwarderService.saveForwarder(request);
    return ResponseEntity.ok("Shipping Line created successfully");
  }

  @PreAuthorize("hasRole('OPERATOR')")
  @GetMapping("")
  public ResponseEntity<?> getForwarders(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Forwarder> pages = forwarderService.getForwarders(request);

    PaginationResponse<ForwarderDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Forwarder> forwarders = pages.getContent();
    List<ForwarderDto> forwardersDto = new ArrayList<>();
    forwarders.forEach(forwarder -> forwardersDto.add(ForwarderMapper.toForwarderDto(forwarder)));
    response.setContents(forwardersDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('OPERATOR') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getForwarder(@PathVariable Long id) {
    Forwarder forwarder = forwarderService.getForwarder(id);
    ForwarderDto forwarderDto = ForwarderMapper.toForwarderDto(forwarder);
    return ResponseEntity.ok(forwarderDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PutMapping("")
  public ResponseEntity<?> updateForwarder(@Valid @RequestBody ForwarderRequest request) {
    Forwarder forwarder = forwarderService.updateForwarder(request);
    ForwarderDto forwarderDto = ForwarderMapper.toForwarderDto(forwarder);
    return ResponseEntity.ok(forwarderDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editForwarder(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Forwarder forwarder = forwarderService.editForwarder(id, updates);
    ForwarderDto forwarderDto = ForwarderMapper.toForwarderDto(forwarder);
    return ResponseEntity.ok(forwarderDto);
  }

  @Transactional
  @PreAuthorize("hasRole('OPERATOR')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteForwarder(@PathVariable Long id) {
    forwarderService.removeForwarder(id);
    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }
}

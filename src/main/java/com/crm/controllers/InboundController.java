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
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Inbound;
import com.crm.models.dto.InboundDto;
import com.crm.models.mapper.InboundMapper;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.InboundService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inbound")
public class InboundController {

  @Autowired
  private InboundService inboundService;

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getInbounds(@Valid PaginationRequest request) {

    Page<Inbound> pages = inboundService.getInbounds(request);
    PaginationResponse<InboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Inbound> inbounds = pages.getContent();
    List<InboundDto> inboundsDto = new ArrayList<>();
    inbounds.forEach(inbound -> inboundsDto.add(InboundMapper.toInboundDto(inbound)));
    response.setContents(inboundsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/forwarder/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getInboundsByForwarder(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Inbound> pages = inboundService.getInboundsForwarder(id, request);
    PaginationResponse<InboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Inbound> inbounds = pages.getContent();
    List<InboundDto> inboundsDto = new ArrayList<>();
    inbounds.forEach(inbound -> inboundsDto.add(InboundMapper.toInboundDto(inbound)));
    response.setContents(inboundsDto);

    return ResponseEntity.ok(response);
  }
  
  @GetMapping("/outbound/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getInboundsByOutbound(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Inbound> pages = inboundService.getInboundsByOutbound(id, request);
    PaginationResponse<InboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Inbound> inbounds = pages.getContent();
    List<InboundDto> inboundsDto = new ArrayList<>();
    inbounds.forEach(inbound -> inboundsDto.add(InboundMapper.toInboundDto(inbound)));
    response.setContents(inboundsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getInbound(@PathVariable Long id) {
    Inbound inbound = inboundService.getInboundById(id);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @Transactional
  @PostMapping("/forwarder/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createInbound(@PathVariable Long id, @Valid @RequestBody InboundRequest request) {
    Inbound inbound = inboundService.createInbound(id, request);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> updateInbound(@Valid @RequestBody InboundRequest request) {
    Inbound inbound = inboundService.updateInbound(request);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> editInbound(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {
    Inbound inbound = inboundService.editInbound(updates, id);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeInbound(@PathVariable Long id) {
    inboundService.removeInbound(id);
    return ResponseEntity.ok(new MessageResponse("Inbound has remove successfully"));
  }

}

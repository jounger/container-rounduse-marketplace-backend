package com.crm.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Inbound;
import com.crm.models.dto.InboundDto;
import com.crm.models.mapper.InboundMapper;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.services.InboundService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inbound")
public class InboundController {

  @Autowired
  private InboundService inboundService;

  @GetMapping("")
  @PreAuthorize("hasRole('MODERATOR')")
  public ResponseEntity<?> getInbounds(@Valid PaginationRequest request) {

    return null;
  }

  @PostMapping("/forwarder/{id}")
  public ResponseEntity<?> createInbound(@PathVariable Long id, @Valid @RequestBody InboundRequest request) {
    Inbound inbound = inboundService.createInbound(id, request);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @Transactional
  @PutMapping("")
  public ResponseEntity<?> updateInbound(@Valid @RequestBody InboundRequest request) {
    Inbound inbound = inboundService.updateInbound(request);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

}

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

import com.crm.models.Outbound;
import com.crm.models.dto.OutboundDto;
import com.crm.models.mapper.OutboundMapper;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.OutboundService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/outbound")
public class OutboundController {

  @Autowired
  private OutboundService outBoundService;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getOutbound(@PathVariable Long id) {
    Outbound outbound = outBoundService.getOutboundById(id);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @GetMapping("")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getOutbounds(@Valid PaginationRequest request) {

    Page<Outbound> pages = outBoundService.getOutbounds(request);
    PaginationResponse<OutboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Outbound> outbounds = pages.getContent();
    List<OutboundDto> outboundsDto = new ArrayList<>();
    outbounds.forEach(outbound -> outboundsDto.add(OutboundMapper.toOutboundDto(outbound)));
    response.setContents(outboundsDto);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/merchant")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> getOutboundsByMerchant(@Valid PaginationRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long id = userDetails.getId();

    Page<Outbound> pages = outBoundService.getOutboundsByMerchant(id, request);
    PaginationResponse<OutboundDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Outbound> outbounds = pages.getContent();
    List<OutboundDto> outboundsDto = new ArrayList<>();
    outbounds.forEach(outbound -> outboundsDto.add(OutboundMapper.toOutboundDto(outbound)));
    response.setContents(outboundsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PostMapping("")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> createOutbound(@Valid @RequestBody OutboundRequest request) {
    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();
    Outbound outbound = outBoundService.createOutbound(userId, request);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @PutMapping("")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> updateOutboundDto(@Valid @RequestBody OutboundRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Outbound outbound = outBoundService.updateOutbound(userId, request);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> editOutbound(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Outbound outbound = outBoundService.editOutbound(updates, id, userId);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> removeOutbound(@PathVariable Long id) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    outBoundService.removeOutbound(id, userId);
    return ResponseEntity.ok(new MessageResponse("Outbound has remove successfully"));
  }
}

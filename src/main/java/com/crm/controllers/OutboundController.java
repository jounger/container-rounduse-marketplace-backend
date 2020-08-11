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

import com.crm.models.Outbound;
import com.crm.models.dto.OutboundDto;
import com.crm.models.mapper.OutboundMapper;
import com.crm.payload.request.OutboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
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

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/filter")
  public ResponseEntity<?> searchOutbounds(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<Outbound> pages = outBoundService.searchOutbounds(request, search);
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

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Outbound> pages = outBoundService.getOutboundsByMerchant(username, request);
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
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    Outbound outbound = outBoundService.createOutbound(username, request);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> editOutbound(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Outbound outbound = outBoundService.editOutbound(updates, id, username);
    OutboundDto outboundDto = new OutboundDto();
    outboundDto = OutboundMapper.toOutboundDto(outbound);
    return ResponseEntity.ok(outboundDto);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MERCHANT')")
  public ResponseEntity<?> removeOutbound(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    outBoundService.removeOutbound(id, username);
    return ResponseEntity.ok(new MessageResponse("Outbound has removed successfully"));
  }
}

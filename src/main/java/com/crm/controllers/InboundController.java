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
import com.crm.models.Inbound;
import com.crm.models.dto.InboundDto;
import com.crm.models.mapper.InboundMapper;
import com.crm.payload.request.InboundRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.InboundService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inbound")
public class InboundController {

  private static final Logger logger = LoggerFactory.getLogger(InboundController.class);

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

  @GetMapping("/filter")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> searchInbounds(@Valid PaginationRequest request,
      @RequestParam(value = "search") String search) {

    Page<Inbound> pages = inboundService.searchInbounds(request, search);
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

  @GetMapping("/forwarder")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getInboundsByForwarder(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Inbound> pages = inboundService.getInboundsByForwarder(username, request);
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

  @GetMapping("/container/{id}")
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT')")
  public ResponseEntity<?> getInboundByContainer(@PathVariable Long id) {
    Inbound inbound = inboundService.getInboundByContainer(id);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);
    return ResponseEntity.ok(inboundDto);
  }

  @GetMapping("/outbound-match/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> getInboundsByOutboundAndForwarder(@PathVariable Long id, @Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Inbound> pages = inboundService.getInboundsByOutboundAndForwarder(id, username, request);
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
  @PostMapping("")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> createInbound(@Valid @RequestBody InboundRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Inbound inbound = inboundService.createInbound(username, request);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);

    // Set default response body
    DefaultResponse<InboundDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_INBOUND_SUCCESSFULLY);
    defaultResponse.setData(inboundDto);

    logger.info("User {} createInbound with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> editInbound(@RequestBody Map<String, Object> updates, @PathVariable("id") Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Inbound inbound = inboundService.editInbound(updates, id, username);
    InboundDto inboundDto = new InboundDto();
    inboundDto = InboundMapper.toInboundDto(inbound);

    // Set default response body
    DefaultResponse<InboundDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_INBOUND_SUCCESSFULLY);
    defaultResponse.setData(inboundDto);

    logger.info("User {} editInbound from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('FORWARDER')")
  public ResponseEntity<?> removeInbound(@PathVariable Long id) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    inboundService.removeInbound(id, username);

    // Set default response body
    DefaultResponse<InboundDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_INBOUND_SUCCESSFULLY);

    logger.info("User {} deleteInbound with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}

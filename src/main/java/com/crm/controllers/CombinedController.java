package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Combined;
import com.crm.models.dto.CombinedDto;
import com.crm.models.mapper.CombinedMapper;
import com.crm.payload.request.CombinedRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.CombinedService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/combined")
public class CombinedController {

  @Autowired
  private CombinedService combinedService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @PostMapping("/bid/{id}")
  public ResponseEntity<?> createCombined(@PathVariable("id") Long id, @Valid @RequestBody CombinedRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Combined combined = combinedService.createCombined(id, username, request);
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateCombinedToDriver(combined);
    // END NOTIFICATION

    return ResponseEntity.ok(combinedDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getCombined(@PathVariable("id") Long id) {
    Combined combined = combinedService.getCombined(id);
    CombinedDto combinedDto = CombinedMapper.toCombinedDto(combined);
    return ResponseEntity.ok(combinedDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/user")
  public ResponseEntity<?> getCombinedsByUser(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Combined> pages = combinedService.getCombinedsByUser(username, request);

    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/bidding-document/{id}")
  public ResponseEntity<?> getCombinedsByBiddingDocument(@PathVariable("id") Long id,
      @Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();

    Page<Combined> pages = combinedService.getCombinedsByBiddingDocument(id, username, request);

    PaginationResponse<CombinedDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Combined> combineds = pages.getContent();
    List<CombinedDto> combinedsDto = new ArrayList<>();
    combineds.forEach(combined -> combinedsDto.add(CombinedMapper.toCombinedDto(combined)));
    response.setContents(combinedsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER') or hasRole('MERCHANT') or hasRole('DRIVER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editCombined(@PathVariable("id") Long id, @RequestBody String isCanceled) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    String username = userDetails.getUsername();
    Combined Combined = combinedService.editCombined(id, username, isCanceled);
    CombinedDto CombinedDto = CombinedMapper.toCombinedDto(Combined);
    return ResponseEntity.ok(CombinedDto);
  }
}

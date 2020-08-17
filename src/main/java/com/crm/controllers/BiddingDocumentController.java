package com.crm.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.BiddingDocument;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.mapper.BiddingDocumentMapper;
import com.crm.payload.request.BiddingDocumentRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BiddingDocumentService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bidding-document")
public class BiddingDocumentController {

  private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

  @Autowired
  private BiddingDocumentService biddingDocumentService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @PostMapping("")
  public ResponseEntity<?> createBiddingDocument(@Valid @RequestBody BiddingDocumentRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    BiddingDocument biddingDocument = biddingDocumentService.createBiddingDocument(username, request);
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateBiddingDocumentToForwarder(biddingDocument);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<BiddingDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_BIDDING_DUCUMENT_SUCCESSFULLY);
    defaultResponse.setData(biddingDocumentDto);

    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/combined")
  public ResponseEntity<?> getBiddingDocumentsByExistCombined(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<BiddingDocument> pages = biddingDocumentService.getBiddingDocumentsByExistCombined(username, request);

    PaginationResponse<BiddingDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BiddingDocument> biddingDocuments = pages.getContent();
    List<BiddingDocument> result = biddingDocumentService.updateExpiredBiddingDocumentFromList(biddingDocuments);
    List<BiddingDocumentDto> biddingDocumentsDto = new ArrayList<>();
    result.forEach(
        biddingDocument -> biddingDocumentsDto.add(BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument)));
    response.setContents(biddingDocumentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("")
  public ResponseEntity<?> getBiddingDocuments(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<BiddingDocument> pages = biddingDocumentService.getBiddingDocuments(username, request);

    PaginationResponse<BiddingDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BiddingDocument> biddingDocuments = pages.getContent();
    List<BiddingDocument> result = biddingDocumentService.updateExpiredBiddingDocumentFromList(biddingDocuments);
    List<BiddingDocumentDto> biddingDocumentsDto = new ArrayList<>();
    result.forEach(
        biddingDocument -> biddingDocumentsDto.add(BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument)));
    response.setContents(biddingDocumentsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBiddingDocument(@PathVariable Long id) {
    BiddingDocument biddingDocument = biddingDocumentService.getBiddingDocument(id);
    List<BiddingDocument> result = biddingDocumentService
        .updateExpiredBiddingDocumentFromList(Arrays.asList(biddingDocument));
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(result.get(0));
    return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/bid/{id}")
  public ResponseEntity<?> getBiddingDocumentByBid(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    BiddingDocument biddingDocument = biddingDocumentService.getBiddingDocumentByBid(id, username);
    List<BiddingDocument> result = biddingDocumentService
        .updateExpiredBiddingDocumentFromList(Arrays.asList(biddingDocument));
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(result.get(0));
    return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/combined/{id}")
  public ResponseEntity<?> getBiddingDocumentByCombined(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    BiddingDocument biddingDocument = biddingDocumentService.getBiddingDocumentByCombined(id, username);
    List<BiddingDocument> result = biddingDocumentService
        .updateExpiredBiddingDocumentFromList(Arrays.asList(biddingDocument));
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(result.get(0));
    return ResponseEntity.ok(biddingDocumentDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @GetMapping("/inbound/{id}")
  public ResponseEntity<?> getBiddingDocumentsByInbound(@PathVariable("id") Long id, @Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<BiddingDocument> pages = biddingDocumentService.getBiddingDocumentsByInbound(id, username, request);

    PaginationResponse<BiddingDocumentDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<BiddingDocument> biddingDocuments = pages.getContent();
    List<BiddingDocument> result = biddingDocumentService.updateExpiredBiddingDocumentFromList(biddingDocuments);
    List<BiddingDocumentDto> biddingDocumentsDto = new ArrayList<>();
    result.forEach(
        biddingDocument -> biddingDocumentsDto.add(BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument)));
    response.setContents(biddingDocumentsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBiddingDocument(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    BiddingDocument biddingDocument = biddingDocumentService.editBiddingDocument(id, username, updates);
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);

    // Set default response body
    DefaultResponse<BiddingDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BIDDING_DUCUMENT_SUCCESSFULLY);
    defaultResponse.setData(biddingDocumentDto);

    logger.info("User {} editBiddingDocument from id {} with request: {}", username, id,
        updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBiddingDocument(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    biddingDocumentService.removeBiddingDocument(id, username);

    // Set default response body
    DefaultResponse<BiddingDocumentDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_BIDDING_DUCUMENT_SUCCESSFULLY);

    logger.info("User {} deleteBiddingDocument bidding document id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }
}

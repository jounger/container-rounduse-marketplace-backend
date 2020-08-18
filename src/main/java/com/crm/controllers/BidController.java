package com.crm.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.common.SuccessMessage;
import com.crm.models.Bid;
import com.crm.models.dto.BidDto;
import com.crm.models.mapper.BidMapper;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReplaceContainerRequest;
import com.crm.payload.response.DefaultResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BidService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bid")
public class BidController {

  private static final Logger logger = LoggerFactory.getLogger(Bid.class);

  @Autowired
  private BidService bidService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

  @Autowired
  @Qualifier("cachedThreadPool")
  private ExecutorService executorService;

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping("/bidding-document/{id}")
  public ResponseEntity<?> createBid(@PathVariable Long id, @Valid @RequestBody BidRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Bid bid = bidService.createBid(id, username, request);
    BidDto bidDto = BidMapper.toBidDto(bid);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastCreateBidToMerchant(bid);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<BidDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.CREATE_BID_SUCCESSFULLY);
    defaultResponse.setData(bidDto);

    logger.info("User {} createBid with request: {}", username, request.toString());
    return ResponseEntity.status(HttpStatus.CREATED).body(defaultResponse);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBid(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.getBid(id, username);
    List<Bid> result = bidService.updateExpiredBidFromList(Arrays.asList(bid));
    BidDto bidDto = BidMapper.toBidDto(result.get(0));
    return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/combined/bidding-document/{id}")
  public ResponseEntity<?> getBidByBiddingDocumentAndExistCombined(@PathVariable Long id,
      @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();

    Page<Bid> pages = bidService.getBidsByBiddingDocumentAndExistCombined(id, username, request);

    PaginationResponse<BidDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Bid> bids = pages.getContent();
    List<Bid> result = bidService.updateExpiredBidFromList(bids);
    List<BidDto> bidsDto = new ArrayList<>();
    result.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
    response.setContents(bidsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/bidding-document/{id}")
  public ResponseEntity<?> getBidsByBiddingDocument(@PathVariable Long id, @Valid PaginationRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<Bid> pages = bidService.getBidsByBiddingDocument(id, username, request);

    PaginationResponse<BidDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Bid> bids = pages.getContent();
    List<Bid> result = bidService.updateExpiredBidFromList(bids);
    List<BidDto> bidsDto = new ArrayList<>();
    result.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
    response.setContents(bidsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @GetMapping("/forwarder")
  public ResponseEntity<?> getBidsByForwarder(@Valid PaginationRequest request) {

    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Page<Bid> pages = bidService.getBidsByForwarder(username, request);

    PaginationResponse<BidDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Bid> bids = pages.getContent();
    List<Bid> result = bidService.updateExpiredBidFromList(bids);
    List<BidDto> bidsDto = new ArrayList<>();
    result.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
    response.setContents(bidsDto);

    return ResponseEntity.ok(response);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBid(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.getBid(id, username);
    String status = bid.getStatus();
    Bid bidEdit = bidService.editBid(id, username, updates);
    BidDto bidDto = BidMapper.toBidDto(bidEdit);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastEditBidToMerchantOrForwarder(status, bidEdit);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<BidDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BID_SUCCESSFULLY);
    defaultResponse.setData(bidDto);

    logger.info("User {} editBid from id {} with request: {}", username, id, updates.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping(value = "/{id}/container/{contId}")
  public ResponseEntity<?> addContainers(@PathVariable("id") Long id,@Valid @RequestBody BidRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.addContainer(id, username, request);
    BidDto bidDto = BidMapper.toBidDto(bid);

    // Set default response body
    DefaultResponse<BidDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BID_SUCCESSFULLY);
    defaultResponse.setData(bidDto);

    logger.info("User {} addContainer into bid id {} with request: {}", username, id, request.toString());
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping(value = "/{id}/container/{contId}")
  public ResponseEntity<?> removeContainer(@PathVariable("id") Long id, @PathVariable("contId") Long containerId) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.removeContainer(id, username, containerId);
    BidDto bidDto = BidMapper.toBidDto(bid);

    // Set default response body
    DefaultResponse<BidDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.EDIT_BID_SUCCESSFULLY);
    defaultResponse.setData(bidDto);

    logger.info("User {} removeContainer from bid id {} with container id: {}", username, id, containerId);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PatchMapping(value = "/{id}/container", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> replaceContainer(@PathVariable("id") Long id, @RequestBody ReplaceContainerRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.replaceContainer(id, username, request);
    BidDto bidDto = BidMapper.toBidDto(bid);

    logger.info("User {} replaceContainer from bid id {} with request {}", username, id, request.toString());
    return ResponseEntity.ok(bidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeBid(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.getBid(id, username);
    bidService.removeBid(id, username);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastRemoveBidToMerchant(bid);
    // END NOTIFICATION

    // Set default response body
    DefaultResponse<BidDto> defaultResponse = new DefaultResponse<>();
    defaultResponse.setMessage(SuccessMessage.DELETE_BID_SUCCESSFULLY);

    logger.info("User {} deleteBid with id {}", username, id);
    return ResponseEntity.status(HttpStatus.OK).body(defaultResponse);
  }

}

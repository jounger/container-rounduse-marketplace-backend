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

import com.crm.models.Bid;
import com.crm.models.dto.BidDto;
import com.crm.models.mapper.BidMapper;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReplaceContainerRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BidService;
import com.crm.websocket.controller.NotificationBroadcast;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bid")
public class BidController {

  @Autowired
  private BidService bidService;

  @Autowired
  private NotificationBroadcast notificationBroadcast;

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

    return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBid(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.getBid(id, username);
    BidDto bidDto = BidMapper.toBidDto(bid);
    return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @GetMapping("/bidding-document/{id}")
  public ResponseEntity<?> getBidByBiddingDocumentAndForwarder(@PathVariable Long id) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.getBidByBiddingDocumentAndForwarder(id, username);
    BidDto bidDto = BidMapper.toBidDto(bid);
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
    List<BidDto> bidsDto = new ArrayList<>();
    bids.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
    response.setContents(bidsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @GetMapping("/merchant/{id}")
  public ResponseEntity<?> getBidsByBiddingDocument(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Bid> pages = bidService.getBidsByBiddingDocument(id, request);

    PaginationResponse<BidDto> response = new PaginationResponse<>();
    response.setPageNumber(request.getPage());
    response.setPageSize(request.getLimit());
    response.setTotalElements(pages.getTotalElements());
    response.setTotalPages(pages.getTotalPages());

    List<Bid> bids = pages.getContent();
    List<BidDto> bidsDto = new ArrayList<>();
    bids.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
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
    List<BidDto> bidsDto = new ArrayList<>();
    bids.forEach(bid -> bidsDto.add(BidMapper.toBidDto(bid)));
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
    BidDto BidDto = BidMapper.toBidDto(bidEdit);

    // CREATE NOTIFICATION
    notificationBroadcast.broadcastEditBidToMerchantOrForwarder(status, bidEdit);
    // END NOTIFICATION

    return ResponseEntity.ok(BidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping(value = "/{id}/container/{contId}")
  public ResponseEntity<?> addContainer(@PathVariable("id") Long id, @PathVariable("contId") Long containerId) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.addContainer(id, username, containerId);
    BidDto BidDto = BidMapper.toBidDto(bid);

    return ResponseEntity.ok(BidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping(value = "/{id}/container/{contId}")
  public ResponseEntity<?> removeContainer(@PathVariable("id") Long id, @PathVariable("contId") Long containerId) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.removeContainer(id, username, containerId);
    BidDto BidDto = BidMapper.toBidDto(bid);

    return ResponseEntity.ok(BidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PatchMapping(value = "/{id}/container", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> replaceContainer(@PathVariable("id") Long id, @RequestBody ReplaceContainerRequest request) {
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String username = userDetails.getUsername();
    Bid bid = bidService.replaceContainer(id, username, request);
    BidDto BidDto = BidMapper.toBidDto(bid);

    return ResponseEntity.ok(BidDto);
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

    return ResponseEntity.ok(new MessageResponse("Bid deleted successfully."));
  }

}

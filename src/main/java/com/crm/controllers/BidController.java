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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.crm.models.Bid;
import com.crm.models.dto.BidDto;
import com.crm.models.mapper.BidMapper;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.security.services.UserDetailsImpl;
import com.crm.services.BidService;
import com.crm.websocket.controller.NotificationController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bid")
public class BidController {

  @Autowired
  private BidService bidService;

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping("/bidding-document/{id}")
  public ResponseEntity<?> createBid(@PathVariable Long id, @Valid @RequestBody BidRequest request) {

    UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();
    Long userId = userDetails.getId();

    Bid bid = bidService.createBid(id, userId, request);
    BidDto bidDto = BidMapper.toBidDto(bid);

    // CREATE NOTIFICATION
    NotificationController.broadcastCreateBidToMerchant(bid);
    // END NOTIFICATION

    return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBid(@PathVariable Long id) {
    Bid bid = bidService.getBid(id);
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
  @GetMapping("/forwarder/{id}")
  public ResponseEntity<?> getBidsByForwarder(@PathVariable Long id, @Valid PaginationRequest request) {

    Page<Bid> pages = bidService.getBidsByForwarder(id, request);

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
  @PreAuthorize("hasRole('FORWARDER')")
  @PutMapping("")
  public ResponseEntity<?> updateBid(@Valid @RequestBody BidRequest request) {
    Bid bid = bidService.updateBid(request);
    BidDto bidDto = BidMapper.toBidDto(bid);
    return ResponseEntity.ok(bidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBid(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Bid bid = bidService.getBid(id);
    String status = bid.getStatus();
    Bid bidEdit = bidService.editBid(id, updates);
    BidDto BidDto = BidMapper.toBidDto(bidEdit);

    // CREATE NOTIFICATION
    NotificationController.broadcastEditBidToMerchantOrForwarder(status, bidEdit);
    // END NOTIFICATION

    return ResponseEntity.ok(BidDto);
  }

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeBid(@PathVariable Long id) {
    Bid bid = bidService.getBid(id);
    bidService.removeBid(id);

    // CREATE NOTIFICATION
    NotificationController.broadcastRemoveBidToMerchant(bid);
    // END NOTIFICATION

    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }

}

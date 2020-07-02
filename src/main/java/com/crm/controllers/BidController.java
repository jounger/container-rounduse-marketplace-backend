package com.crm.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingNotificationType;
import com.crm.models.Bid;
import com.crm.models.BiddingNotification;
import com.crm.models.Merchant;
import com.crm.models.dto.BidDto;
import com.crm.models.mapper.BidMapper;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.BiddingNotificationRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.response.MessageResponse;
import com.crm.payload.response.PaginationResponse;
import com.crm.services.BidService;
import com.crm.services.BiddingNotificationService;
import com.crm.websocket.service.BiddingWebSocketService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/bid")
public class BidController {

  private static final Logger logger = LoggerFactory.getLogger(BiddingDocumentController.class);

  @Autowired
  private BidService bidService;

  @Autowired
  private BiddingNotificationService biddingNotificationService;

  @Autowired
  private BiddingWebSocketService biddingWebSocketService;

  @Transactional
  @PreAuthorize("hasRole('FORWARDER')")
  @PostMapping("")
  public ResponseEntity<?> createBid(@Valid @RequestBody BidRequest request) {

    Bid bid = bidService.createBid(request);
    BidDto bidDto = BidMapper.toBidDto(bid);

    // CREATE NOTIFICATION
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    PaginationRequest paging = new PaginationRequest();
    paging.setPage(0);
    paging.setLimit(100);
    Merchant offeree = bid.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database

    notifyRequest.setRecipient(offeree.getUsername());
    notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
    notifyRequest.setMessage(String.format("You got a new Bid from %s", bid.getBidder().getUsername()));
    notifyRequest.setType(EnumBiddingNotificationType.ADDED.name());
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to forwarders

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.broadcastBiddingNotifyToUser(notification);

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
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    PaginationRequest paging = new PaginationRequest();
    paging.setPage(0);
    paging.setLimit(100);
    Merchant offeree = bidEdit.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database
    if (bidEdit.getStatus().equals(status)) {

      notifyRequest.setRecipient(offeree.getUsername());
      notifyRequest.setRelatedResource(bidEdit.getBiddingDocument().getId());
      notifyRequest.setMessage(String.format("Bid have been MODIFIED by %s", bidEdit.getBidder().getUsername()));
      notifyRequest.setType(EnumBiddingNotificationType.MODIFIED.name());

    } else {
      if (bidEdit.getStatus().equals(EnumBidStatus.ACCEPTED.name())) {

        notifyRequest.setRecipient(bid.getBidder().getUsername());
        notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format("Your Bid have ACCEPTED from %s", offeree.getUsername()));
        notifyRequest.setType(EnumBiddingNotificationType.ACCEPTED.name());

      } else if (bidEdit.getStatus().equals(EnumBidStatus.REJECTED.name())) {

        notifyRequest.setRecipient(bid.getBidder().getUsername());
        notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
        notifyRequest.setMessage(String.format("Your Bid have REJECTED from %s", offeree.getUsername()));
        notifyRequest.setType(EnumBiddingNotificationType.REJECTED.name());
      }
    }
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to forwarders

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.broadcastBiddingNotifyToUser(notification);

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
    BiddingNotificationRequest notifyRequest = new BiddingNotificationRequest();
    PaginationRequest paging = new PaginationRequest();
    paging.setPage(0);
    paging.setLimit(100);
    Merchant offeree = bid.getBiddingDocument().getOfferee();

    // Create new message notifications and save to Database

    notifyRequest.setRecipient(offeree.getUsername());
    notifyRequest.setRelatedResource(bid.getBiddingDocument().getId());
    notifyRequest.setMessage(String.format("Bid have been REMOVED by %s", bid.getBidder().getUsername()));
    notifyRequest.setType(EnumBiddingNotificationType.REMOVED.name());
    BiddingNotification notification = biddingNotificationService.createBiddingNotification(notifyRequest);

    // Asynchronous send notification to forwarders

    logger.info("notification : {}", notification.getId());
    biddingWebSocketService.broadcastBiddingNotifyToUser(notification);

    // END NOTIFICATION

    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }

}

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.crm.services.BidService;

@CrossOrigin(origins="*", maxAge=3600)
@RestController
@RequestMapping("/api/bid")
public class BidController {

  @Autowired
  private BidService bidService;

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
    bids.forEach(
        bid -> bidsDto.add(BidMapper.toBidDto(bid)));
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
    bids.forEach(
        bid -> bidsDto.add(BidMapper.toBidDto(bid)));
    response.setContents(bidsDto);

    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasRole('MERCHANT') or hasRole('FORWARDER')")
  @GetMapping("/{id}")
  public ResponseEntity<?> getBid(@PathVariable Long id) {
    Bid bid = bidService.getBid(id);
    BidDto bidDto = BidMapper.toBidDto(bid);
    return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('FORWARDER')")
  @PutMapping("")
  public ResponseEntity<?> updateBid(@Valid @RequestBody BidRequest request) {
     Bid bid = bidService.updateBid(request);     
     BidDto bidDto = BidMapper.toBidDto(bid);
     return ResponseEntity.ok(bidDto);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> editBid(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
    Bid Bid = bidService.editBid(id, updates);
    BidDto BidDto = BidMapper.toBidDto(Bid);
    return ResponseEntity.ok(BidDto);
  }

  @PreAuthorize("hasRole('MERCHANT')")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteBid(@PathVariable Long id) {
    bidService.removeBid(id);
    return ResponseEntity.ok(new MessageResponse("Bidding document deleted successfully."));
  }
}

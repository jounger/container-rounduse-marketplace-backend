package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Container;
import com.crm.models.Forwarder;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.services.BidService;

@Service
public class BidServiceImpl implements BidService {

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private BiddingDocumentRepository biddingDocumentRepository;

  @Autowired
  private ForwarderRepository forwarderRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Override
  public void createBid(BidRequest request) {
    Bid bid = new Bid();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocumentId())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    bid.setBiddingDocument(biddingDocument);

    Forwarder bidder = forwarderRepository.findById(request.getFowarderId())
        .orElseThrow(() -> new NotFoundException("Forwarer is not found."));
    bid.setBidder(bidder);

    Container container = containerRepository.findById(request.getContainerId())
        .orElseThrow(() -> new NotFoundException("Container is not found."));
    bid.setContainer(container);

    bid.setBidPrice(request.getBidPrice());
    bid.setCurrentBidPrice(request.getCurrentBidPrice());

    LocalDateTime bidDate = Tool.convertToLocalDateTime(request.getBidDate());
    bid.setBidDate(bidDate);

    LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(request.getBidValidityPeriod());
    bid.setBidValidityPeriod(bidValidityPeriod);

    bid.setStatus(EnumBidStatus.PENDING);

    bidRepository.save(bid);
  }

  @Override
  public Bid getBid(Long id) {
    Bid bid = new Bid();
    bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException("Bid is not found."));
    return bid;
  }

  @Override
  public Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request) {
    Page<Bid> bids = bidRepository.getBidsByBiddingDocument(id, request);
    return bids;
  }

  @Override
  public Page<Bid> getBidsByForwarder(Long id, PaginationRequest request) {
    Page<Bid> bids = bidRepository.getBidsByForwarder(id, request);
    return bids;
  }

  @Override
  public void removeBid(Long id) {
    if (bidRepository.existsById(id)) {
      bidRepository.deleteById(id);
    } else {
      throw new NotFoundException("Bid is not found.");
    }
  }

  @Override
  public Bid updateBid(BidRequest request) {
    Bid bid = bidRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Bid is not found."));

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocumentId())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    bid.setBiddingDocument(biddingDocument);

    Forwarder bidder = forwarderRepository.findById(request.getFowarderId())
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    bid.setBidder(bidder);

    Container container = containerRepository.findById(request.getContainerId())
        .orElseThrow(() -> new NotFoundException("Container is not found."));
    bid.setContainer(container);

    bid.setBidPrice(request.getBidPrice());
    bid.setCurrentBidPrice(request.getCurrentBidPrice());

    LocalDateTime bidDate = Tool.convertToLocalDateTime(request.getBidDate());
    bid.setBidDate(bidDate);

    LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(request.getBidValidityPeriod());
    bid.setBidValidityPeriod(bidValidityPeriod);

    bid.setStatus(EnumBidStatus.PENDING);

    bidRepository.save(bid); 
    
     
     
    return bid;
  }

  @Override
  public Bid editBid(Long id, Map<String, Object> updates) {
    Bid bid = bidRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Bid is not found."));
    
    try {
      String bidPriceString = (String) updates.get("bid_price");
      if(bidPriceString != null) {
        Float bidPrice = Float.parseFloat(bidPriceString);
        bid.setBidPrice(bidPrice);
      }
      
      String currentBidPriceString = (String) updates.get("current_bid_price");
      if(currentBidPriceString != null) {
        Float currentBidPrice = Float.parseFloat(currentBidPriceString);
        bid.setCurrentBidPrice(currentBidPrice);
      }
    } catch (Exception e) {
      throw new InternalException("Parameter must be Float");
    }
    
    String bidDateString = (String) updates.get("bid_date");
    if(bidDateString != null) {
      LocalDateTime bidDate = Tool.convertToLocalDateTime(bidDateString);
      bid.setBidDate(bidDate);
    }
    
    String bidValidityPeriodString = (String) updates.get("bid_validity_period");
    if(bidValidityPeriodString != null) {
      LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(bidValidityPeriodString);
      bid.setBidValidityPeriod(bidValidityPeriod);
    }
    
    String statusString = (String) updates.get("ebid_status_name");
    if(statusString != null) {
      EnumBidStatus status = EnumBidStatus.findByName(statusString);
      bid.setStatus(status);
    }
    return bid;
  }

}

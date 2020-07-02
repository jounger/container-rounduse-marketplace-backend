package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.Booking;
import com.crm.models.Container;
import com.crm.models.Forwarder;
import com.crm.models.Outbound;
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
  public Bid createBid(BidRequest request) {
    Bid bid = new Bid();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocument())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    bid.setBiddingDocument(biddingDocument);

    Forwarder bidder = forwarderRepository.findByUsername(request.getBidder())
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    bid.setBidder(bidder);

    List<Bid> bids = biddingDocument.getBids();
    bids.forEach(bidOfBidding -> {
      if (bidOfBidding.getBidder() == bidder) {
        throw new DuplicateRecordException("You can only create 1 bid for 1 bidding document.");
      }
    });

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException("Number of containers is more than needed.");
    }
    List<Container> suitableContainers = containerRepository.findByOutbound(outbound.getShippingLine().getCompanyCode(),
        outbound.getContainerType().getName(),
        Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.PUBLISHED.name()), outbound.getPackingTime(),
        booking.getCutOffTime(), booking.getPortOfLoading().getNameCode());
    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException("Container is not found."));
      if (suitableContainers.contains(container)) {
        bid.getContainers().add(container);
        container.setStatus(EnumSupplyStatus.BIDDING.name());
      } else {
        throw new NotFoundException("Container is not suitable.");
      }
    });

    Double bidPrice = request.getBidPrice();
    if (bidPrice <= biddingDocument.getBidFloorPrice()) {
      throw new InternalException("Bid price must be equal or greater than floor price.");
    }
    bid.setBidPrice(request.getBidPrice());

    LocalDateTime bidDate = LocalDateTime.now();
    bid.setBidDate(bidDate);

    LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(request.getBidValidityPeriod());
    if (bidValidityPeriod != null) {
      if (bidValidityPeriod.isAfter(LocalDateTime.now().plusDays(1))
          || bidValidityPeriod.isEqual(LocalDateTime.now().plusDays(1))) {
        bid.setBidValidityPeriod(bidValidityPeriod);
      } else {
        throw new InternalException("Bid validity period must be at least 1 day after now.");
      }
    } else {
      bid.setBidValidityPeriod(LocalDateTime.now().plusDays(1));
    }

    bid.setStatus(EnumBidStatus.PENDING.name());

    bidRepository.save(bid);

    biddingDocument.getBids().add(bid);
    if (bid.getBidPrice() < biddingDocument.getPriceLeadership()) {
      biddingDocument.setPriceLeadership(bid.getBidPrice());
    }
    biddingDocumentRepository.save(biddingDocument);

    return bid;
  }

  @Override
  public Bid getBid(Long id) {
    Bid bid = new Bid();
    bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException("Bid is not found."));
    return bid;
  }

  @Override
  public Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request) {
    Page<Bid> bids = bidRepository.getBidsByBiddingDocument(id,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    return bids;
  }

  @Override
  public Page<Bid> getBidsByForwarder(Long id, PaginationRequest request) {
    Page<Bid> bids = bidRepository.getBidsByForwarder(id,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    return bids;
  }

  @Override
  public Bid updateBid(BidRequest request) {
    Bid bid = bidRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Bid is not found."));

    Set<Container> oldContainers = bid.getContainers();
    oldContainers.forEach(container -> {
      container.setStatus(EnumSupplyStatus.CREATED.name());
      containerRepository.save(container);
    });

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(request.getBiddingDocument())
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    bid.setBiddingDocument(biddingDocument);

    Forwarder bidder = forwarderRepository.findByUsername(request.getBidder())
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    bid.setBidder(bidder);

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException("Number of containers is more than needed.");
    }
    List<Container> suitableContainers = containerRepository.findByOutbound(outbound.getShippingLine().getCompanyCode(),
        outbound.getContainerType().getName(),
        Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.PUBLISHED.name(),
            EnumSupplyStatus.BIDDING.name()),
        outbound.getPackingTime(), booking.getCutOffTime(), booking.getPortOfLoading().getNameCode());
    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException("Container is not found."));
      if (suitableContainers.contains(container)) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
      } else {
        throw new NotFoundException("Container is not suitable.");
      }
    });

    bid.setBidPrice(request.getBidPrice());

    /*
     * update bidDate is not allowed LocalDateTime bidDate =
     * Tool.convertToLocalDateTime(request.getBidDate()); bid.setBidDate(bidDate);
     */

    LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(request.getBidValidityPeriod());
    if (bidValidityPeriod != null) {
      if (bidValidityPeriod.isAfter(LocalDateTime.now().plusDays(1))
          || bidValidityPeriod.isEqual(LocalDateTime.now().plusDays(1))) {
        bid.setBidValidityPeriod(bidValidityPeriod);
      } else {
        throw new InternalException("Bid validity period must be at least 1 day after now.");
      }
    } else {
      bid.setBidValidityPeriod(LocalDateTime.now().plusDays(1));
    }

    EnumBidStatus bidStatus = EnumBidStatus.findByName(request.getStatus());
    bid.setStatus(bidStatus.name());
    if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())
        || bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
      bid.setDateOfDecision(LocalDateTime.now());
    }
    if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.CANCELED.name())
        || bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())
        || bid.getStatus().equalsIgnoreCase(EnumBidStatus.EXPIRED.name())) {
      oldContainers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });
    }

    bidRepository.save(bid);

    return bid;
  }

  @Override
  public Bid editBid(Long id, Map<String, Object> updates) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException("Bid is not found."));

    Set<Container> oldContainers = bid.getContainers();
    oldContainers.forEach(container -> {
      container.setStatus(EnumSupplyStatus.CREATED.name());
      containerRepository.save(container);
    });

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    @SuppressWarnings("unchecked")
    List<String> containersId = (List<String>) updates.get("containers");
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId != null) {
      if (containersId.size() > booking.getUnit()) {
        throw new InternalException("Number of containers is more than needed.");
      }
      List<Container> suitableContainers = containerRepository.findByOutbound(
          outbound.getShippingLine().getCompanyCode(), outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.PUBLISHED.name(),
              EnumSupplyStatus.BIDDING.name()),
          outbound.getPackingTime(), booking.getCutOffTime(), booking.getPortOfLoading().getNameCode());
      containersId.forEach(containerId -> {
        Container container = containerRepository.findById(Long.valueOf(containerId))
            .orElseThrow(() -> new NotFoundException("Container is not found."));
        if (suitableContainers.contains(container)) {
          container.setStatus(EnumSupplyStatus.BIDDING.name());
          bid.getContainers().add(container);
        } else {
          throw new NotFoundException("Container is not suitable.");
        }
      });

    }

    try {
      String bidPriceString = (String) updates.get("bidPrice");
      if (bidPriceString != null) {
        Double bidPrice = Double.parseDouble(bidPriceString);
        bid.setBidPrice(bidPrice);
      }
    } catch (Exception e) {
      throw new InternalException("Parameter must be Double");
    }

    /*
     * update bidDate is not allowed String bidDateString = (String)
     * updates.get("bid_date"); if (bidDateString != null) { LocalDateTime bidDate =
     * Tool.convertToLocalDateTime(bidDateString); bid.setBidDate(bidDate); }
     */

    String bidValidityPeriodString = (String) updates.get("bidValidityPeriod");
    if (bidValidityPeriodString != null) {
      LocalDateTime bidValidityPeriod = Tool.convertToLocalDateTime(bidValidityPeriodString);
      if (bidValidityPeriod != null) {
        if (bidValidityPeriod.isAfter(LocalDateTime.now().plusDays(1))
            || bidValidityPeriod.isEqual(LocalDateTime.now().plusDays(1))) {
          bid.setBidValidityPeriod(bidValidityPeriod);
        } else {
          throw new InternalException("Bid validity period must be at least 1 day after now.");
        }
      } else {
        bid.setBidValidityPeriod(LocalDateTime.now().plusDays(1));
      }
    }

    String statusString = (String) updates.get("status");
    if (statusString != null) {
      EnumBidStatus status = EnumBidStatus.findByName(statusString);
      bid.setStatus(status.name());
      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())
          || bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
        bid.setDateOfDecision(LocalDateTime.now());
      }
      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.CANCELED.name())
          || bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())
          || bid.getStatus().equalsIgnoreCase(EnumBidStatus.EXPIRED.name())) {
        oldContainers.forEach(container -> {
          container.setStatus(EnumSupplyStatus.CREATED.name());
          containerRepository.save(container);
        });
      }
    }

    bidRepository.save(bid);

    return bid;
  }

  @Override
  public void removeBid(Long id) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException("Bid is not found."));
    if (!bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())) {
      Set<Container> oldContainers = bid.getContainers();
      oldContainers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });

      bidRepository.deleteById(id);
    } else {
      throw new NotFoundException("Bid is not found.");
    }
  }

}

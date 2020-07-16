package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
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
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
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

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private SupplierRepository supplierRepository;

  @Autowired
  private SupplierRepository supplierRepository;

  @Override
  public Bid createBid(Long bidDocId, Long id, BidRequest request) {
    Bid bid = new Bid();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(bidDocId)
        .orElseThrow(() -> new NotFoundException("Bidding document is not found."));
    bid.setBiddingDocument(biddingDocument);

    String status = biddingDocument.getStatus();
    if (status.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException("Bidding document was time out.");
    }

    Forwarder bidder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    bid.setBidder(bidder);

    List<Bid> bids = new ArrayList<>(biddingDocument.getBids());
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
    if ((booking.getUnit() > containersId.size() && !biddingDocument.getIsMultipleAward())
        || (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward())) {
      throw new InternalException("Number of containers is less than needed.");
    }
    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException("Container is not found."));
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.BIDDING.name()), outbound.getPackingTime(),
          booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
        bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
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

    bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
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
  public Bid getBidByBiddingDocumentAndForwarder(Long biddingDocumentId, String username) {
    Bid bid = bidRepository.findByBiddingDocumentAndForwarder(biddingDocumentId, username)
        .orElseThrow(() -> new NotFoundException("Bid is not found."));
    return bid;
  }

  @Override
  public Page<Bid> getBidsByBiddingDocument(Long id, PaginationRequest request) {
    Page<Bid> bids = bidRepository.findByBiddingDocument(id,
        PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "id")));
    return bids;
  }

  @Override
  public Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, Long userId, PaginationRequest request) {
    if (!biddingDocumentRepository.existsById(id) || !supplierRepository.existsById(userId)) {
      throw new NotFoundException("User or Bidding document is not found.");
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "id"));
    Page<Bid> bids = bidRepository.findByBiddingDocumentAndExistCombined(id, userId, page);
    return bids;
  }

  @Override
  public Page<Bid> getBidsByForwarder(Long id, PaginationRequest request) {
    Page<Bid> bids = null;
    String status = request.getStatus();
    if (status != null && !status.isEmpty()) {
      bids = bidRepository.findByForwarder(id, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    } else {
      bids = bidRepository.findByForwarder(id,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by("id").descending()));
    }
    return bids;
  }

  @Override
  public Bid updateBid(String username, BidRequest request) {
    Bid bid = bidRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException("Bid is not found."));

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Bid is not found."));
    Role role = user.getRoles().iterator().next();
    if (role.getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      String bidStatus = bid.getStatus();
      if (!bidStatus.equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
        throw new InternalException("Bid only can be edited while in PENDING status.");
      }

      LocalDateTime bidValidityPeriod = bid.getBidValidityPeriod();
      if (bidValidityPeriod.isAfter(LocalDateTime.now())) {
        throw new InternalException("Bid only can be edited after bid validity period.");
      }
    }

    Forwarder bidder = forwarderRepository.findByUsername(request.getBidder())
        .orElseThrow(() -> new NotFoundException("Forwarder is not found."));
    bid.setBidder(bidder);

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException("Number of containers is more than needed.");
    }
    if (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward()) {
      throw new InternalException("Number of containers is less than needed.");
    }
    List<Container> oldContainers = new ArrayList<>(bid.getContainers());
    oldContainers.forEach(container -> {
      container.setStatus(EnumSupplyStatus.CREATED.name());
      containerRepository.save(container);
    });

    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException("Container is not found."));
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.BIDDING.name()), outbound.getPackingTime(),
          booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
        bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
      } else {
        throw new NotFoundException("Container is not suitable.");
      }
    });

    if (bid.getBidPrice() != request.getBidPrice()) {
      bid.setBidPrice(request.getBidPrice());
      bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
    }

    EnumBidStatus eBidStatus = EnumBidStatus.findByName(request.getStatus());
    bid.setStatus(eBidStatus.name());
    if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())) {
      bid.setDateOfDecision(LocalDateTime.now());
      outbound.setStatus(EnumSupplyStatus.COMBINED.name());
      outboundRepository.save(outbound);
    }
    if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
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
  public Bid editBid(Long id, String username, Map<String, Object> updates) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException("Bid is not found."));
    User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Bid is not found."));
    Role role = user.getRoles().iterator().next();

    if (role.getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      String bidStatus = bid.getStatus();
      if (!bidStatus.equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
        throw new InternalException("Bid only can be edited while in PENDING status.");
      }

      LocalDateTime bidValidityPeriod = bid.getBidValidityPeriod();
      if (bidValidityPeriod.isAfter(LocalDateTime.now())) {
        throw new InternalException("Bid only can be edited after bid validity period.");
      }
    }

    List<Container> oldContainers = new ArrayList<>(bid.getContainers());

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    String biddingStatus = biddingDocument.getStatus();
    if (biddingStatus.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException("Bidding document was time out.");
    }

    @SuppressWarnings("unchecked")
    List<String> containersId = (List<String>) updates.get("containers");
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId != null) {
      if (containersId.size() > booking.getUnit()) {
        throw new InternalException("Number of containers is more than needed.");
      }
      if (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward()) {
        throw new InternalException("Number of containers is less than needed.");
      }
      oldContainers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });
      List<Container> suitableContainers = containerRepository.findByOutbound(
          outbound.getShippingLine().getCompanyCode(), outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.BIDDING.name()), outbound.getPackingTime(),
          booking.getCutOffTime(), booking.getPortOfLoading().getNameCode());
      containersId.forEach(containerId -> {
        Container container = containerRepository.findById(Long.valueOf(containerId))
            .orElseThrow(() -> new NotFoundException("Container is not found."));
        if (suitableContainers.contains(container)) {
          container.setStatus(EnumSupplyStatus.BIDDING.name());
          bid.getContainers().add(container);
          containerRepository.save(container);
          bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
        } else {
          throw new NotFoundException("Container is not suitable.");
        }
      });

    }

    String bidPriceString = (String) updates.get("bidPrice");
    if (!Tool.isEqual(bid.getBidPrice(), bidPriceString)) {
      bid.setBidPrice(Double.parseDouble(bidPriceString));
      bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
    }

    String statusString = (String) updates.get("status");
    if (!Tool.isEqual(bid.getStatus(), statusString)) {
      EnumBidStatus status = EnumBidStatus.findByName(statusString);
      bid.setStatus(status.name());

      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
        bid.setDateOfDecision(LocalDateTime.now());
        List<Container> containers = new ArrayList<>(bid.getContainers());
        containers.forEach(container -> {
          container.setStatus(EnumSupplyStatus.CREATED.name());
          containerRepository.save(container);
        });
      }

      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())) {
        Long combinedContainer = containerRepository.countCombinedContainersByBiddingDocument(biddingDocument.getId());
        if (booking.getUnit() < combinedContainer + bid.getContainers().size()) {
          throw new InternalException("Number of containers is more than needed.");
        }
        if (booking.getUnit() == combinedContainer + bid.getContainers().size()) {
          biddingDocument.setStatus(EnumBiddingStatus.COMBINED.name());
          biddingDocumentRepository.save(biddingDocument);
        }
        bid.setDateOfDecision(LocalDateTime.now());
        List<Container> containers = new ArrayList<>(bid.getContainers());
        containers.forEach(container -> {
          container.setStatus(EnumSupplyStatus.COMBINED.name());
          containerRepository.save(container);
        });
        if (bidRepository.isAllAcceptedByBiddingDocument(biddingDocument.getId())) {
          outbound.setStatus(EnumSupplyStatus.COMBINED.name());
          outboundRepository.save(outbound);
        }
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
      List<Container> containers = new ArrayList<>(bid.getContainers());
      containers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });

      bidRepository.deleteById(id);
    } else {
      throw new NotFoundException("Bid is not found.");
    }
  }

}

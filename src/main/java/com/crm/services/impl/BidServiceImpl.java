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
import com.crm.common.ErrorConstant;
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

  @Override
  public Bid createBid(Long bidDocId, Long id, BidRequest request) {
    Bid bid = new Bid();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(bidDocId)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BIDDINGDOCUMENT_NOT_FOUND));
    bid.setBiddingDocument(biddingDocument);

    String status = biddingDocument.getStatus();
    if (status.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorConstant.BIDDINGDOCUMENT_TIME_OUT);
    }

    Forwarder bidder = forwarderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND));
    bid.setBidder(bidder);

    List<Bid> bids = new ArrayList<>(biddingDocument.getBids());
    bids.forEach(bidOfBidding -> {
      if (bidOfBidding.getBidder() == bidder) {
        throw new DuplicateRecordException(ErrorConstant.BID_INVALID_CREATE);
      }
    });

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }
    if ((booking.getUnit() > containersId.size() && !biddingDocument.getIsMultipleAward())
        || (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward())) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }
    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.BIDDING.name()), outbound.getPackingTime(),
          booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
        bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
      } else {
        throw new NotFoundException(ErrorConstant.CONTAINER_NOT_SUITABLE);
      }
    });

    Double bidPrice = request.getBidPrice();
    if (bidPrice <= biddingDocument.getBidFloorPrice()) {
      throw new InternalException(ErrorConstant.BID_INVALID_BID_PRICE);
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
  public Bid getBid(Long id, Long userId) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    if (bid.getBidder().getId() != userId) {
      throw new NotFoundException(ErrorConstant.USER_ACCESS_DENIED);
    }
    return bid;
  }

  @Override
  public Bid getBidByBiddingDocumentAndForwarder(Long biddingDocumentId, Long userId) {
    Bid bid = bidRepository.findByBiddingDocumentAndForwarder(biddingDocumentId, userId)
        .orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
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
    if (!biddingDocumentRepository.existsById(id)) {
      throw new NotFoundException(ErrorConstant.BIDDINGDOCUMENT_NOT_FOUND);
    }if(!supplierRepository.existsById(userId)) {
      throw new NotFoundException(ErrorConstant.USER_NOT_FOUND);
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
  public Bid updateBid(Long userId, BidRequest request) {
    Bid bid = bidRepository.findById(request.getId()).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
    Role role = user.getRoles().iterator().next();
    if (role.getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      String bidStatus = bid.getStatus();
      if (!bidStatus.equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
        throw new InternalException(ErrorConstant.BID_INVALID_EDIT);
      }

      LocalDateTime bidValidityPeriod = bid.getBidValidityPeriod();
      if (bidValidityPeriod.isAfter(LocalDateTime.now())) {
        throw new InternalException(ErrorConstant.BID_EDIT_BEFORE_VALIDITY_TIME);
      }
    }

    Forwarder bidder = forwarderRepository.findByUsername(request.getBidder())
        .orElseThrow(() -> new NotFoundException(ErrorConstant.FORWARDER_NOT_FOUND));
    bid.setBidder(bidder);

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }
    if (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward()) {
      throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
    }
    List<Container> oldContainers = new ArrayList<>(bid.getContainers());
    oldContainers.forEach(container -> {
      container.setStatus(EnumSupplyStatus.CREATED.name());
      containerRepository.save(container);
    });

    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(),
          Arrays.asList(EnumSupplyStatus.CREATED.name(), EnumSupplyStatus.BIDDING.name()), outbound.getPackingTime(),
          booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
        bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
      } else {
        throw new NotFoundException(ErrorConstant.CONTAINER_NOT_SUITABLE);
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

  @SuppressWarnings("unchecked")
  @Override
  public Bid editBid(Long id, Long userId, Map<String, Object> updates) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(ErrorConstant.USER_NOT_FOUND));
    Role role = user.getRoles().iterator().next();

    if (role.getName().equalsIgnoreCase("ROLE_FORWARDER")) {
      String bidStatus = bid.getStatus();
      if (!bidStatus.equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
        throw new InternalException(ErrorConstant.BID_INVALID_EDIT);
      }

      LocalDateTime bidValidityPeriod = bid.getBidValidityPeriod();
      if (bidValidityPeriod.isAfter(LocalDateTime.now())) {
        throw new InternalException(ErrorConstant.BID_EDIT_BEFORE_VALIDITY_TIME);
      }
    }

    List<Container> oldContainers = new ArrayList<>(bid.getContainers());

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    String biddingStatus = biddingDocument.getStatus();
    if (biddingStatus.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorConstant.BIDDINGDOCUMENT_TIME_OUT);
    }

    List<String> containersId = (List<String>) updates.get("containers");
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (updates.get("containers") != null && containersId != null) {
      if (containersId.size() > booking.getUnit()) {
        throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
      }
      if (containersId.size() < booking.getUnit() && !biddingDocument.getIsMultipleAward()) {
        throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
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
            .orElseThrow(() -> new NotFoundException(ErrorConstant.CONTAINER_NOT_FOUND));
        if (suitableContainers.contains(container)) {
          container.setStatus(EnumSupplyStatus.BIDDING.name());
          bid.getContainers().add(container);
          containerRepository.save(container);
          bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
        } else {
          throw new NotFoundException(ErrorConstant.CONTAINER_NOT_SUITABLE);
        }
      });

    }

    String bidPriceString = String.valueOf(updates.get("bidPrice"));
    if (updates.get("bidPrice") != null && !Tool.isEqual(bid.getBidPrice(), bidPriceString)) {
      bid.setBidPrice(Double.parseDouble(bidPriceString));
      bid.setBidValidityPeriod(LocalDateTime.now().plusHours(Constant.BID_VALIDITY_PERIOD));
    }

    String statusString = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(bid.getStatus(), statusString)) {
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
          throw new InternalException(ErrorConstant.CONTAINER_MORE_OR_LESS_THAN_NEEDED);
        }
        if (booking.getUnit() == combinedContainer + bid.getContainers().size()) {
          biddingDocument.setStatus(EnumBiddingStatus.COMBINED.name());
          biddingDocumentRepository.save(biddingDocument);
        }
        bid.setDateOfDecision(LocalDateTime.now());
        if (biddingDocument.getIsMultipleAward()) {
          containersId = (List<String>) updates.get("combinedContainers");
          containersId.forEach(conId -> {
            Long containerId = Long.valueOf(conId);
            bid.getContainers().forEach(container -> {
              if(container.getId() == containerId) {
                container.setStatus(EnumSupplyStatus.COMBINED.name());
                containerRepository.save(container);
              }
            });
          });
          
        } else {
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
  public void removeBid(Long id, Long userId) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorConstant.BID_NOT_FOUND));
    if (bid.getBidder().getId() != userId) {
      throw new NotFoundException("Access denied.");
    }
    if (!bid.getStatus().equalsIgnoreCase(EnumBidStatus.ACCEPTED.name())) {
      List<Container> containers = new ArrayList<>(bid.getContainers());
      containers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });

      bidRepository.deleteById(id);
    } else {
      throw new NotFoundException(ErrorConstant.BID_NOT_FOUND);
    }
  }

}

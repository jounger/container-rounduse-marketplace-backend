package com.crm.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.common.ErrorMessage;
import com.crm.common.Tool;
import com.crm.enums.EnumBidStatus;
import com.crm.enums.EnumBiddingStatus;
import com.crm.enums.EnumSupplyStatus;
import com.crm.exception.DuplicateRecordException;
import com.crm.exception.ForbiddenException;
import com.crm.exception.InternalException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.BiddingDocument;
import com.crm.models.BiddingNotification;
import com.crm.models.Booking;
import com.crm.models.Container;
import com.crm.models.Forwarder;
import com.crm.models.Outbound;
import com.crm.models.Role;
import com.crm.models.User;
import com.crm.payload.request.BidRequest;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ReplaceContainerRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.BiddingDocumentRepository;
import com.crm.repository.BiddingNotificationRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.ForwarderRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.SupplierRepository;
import com.crm.repository.UserRepository;
import com.crm.services.BidService;
import com.crm.services.BiddingDocumentService;

@Service
public class BidServiceImpl implements BidService {

  private static final Logger logger = LoggerFactory.getLogger(BidServiceImpl.class);

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
  private BiddingNotificationRepository biddingNotificationRepository;

  @Autowired
  private BiddingDocumentService biddingDocumentService;

  @Override
  public Bid createBid(Long bidDocId, String username, BidRequest request) {
    Bid bid = new Bid();

    BiddingDocument biddingDocument = biddingDocumentRepository.findById(bidDocId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    bid.setBiddingDocument(biddingDocument);

    // check if bidding document is time out
    String status = biddingDocument.getStatus();
    if (status.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name()) || status.equals(EnumBiddingStatus.EXPIRED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_TIME_OUT);
    }

    Forwarder bidder = forwarderRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.FORWARDER_NOT_FOUND));
    bid.setBidder(bidder);

    // check duplicate forwarder in one bidding document
    List<Bid> bids = new ArrayList<>(biddingDocument.getBids());
    bids.forEach(bidOfBidding -> {
      if (bidOfBidding.getBidder().getId() == bidder.getId()) {
        throw new DuplicateRecordException(ErrorMessage.BID_INVALID_CREATE);
      }
    });

    List<Long> containersId = request.getContainers();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    if (containersId.size() > booking.getUnit()) {
      throw new InternalException(ErrorMessage.CONTAINER_MORE_THAN_NEEDED);
    }
    if ((booking.getUnit() > containersId.size() && !biddingDocument.getIsMultipleAward())) {
      throw new InternalException(ErrorMessage.CONTAINER_LESS_THAN_NEEDED);
    }
    containersId.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
      if (container.getBillOfLading().getFreeTime().isBefore(LocalDateTime.now())) {
        throw new InternalException(ErrorMessage.INBOUND_INVALID_FREETIME);
      }
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(), Arrays.asList(EnumSupplyStatus.CREATED.name()),
          outbound.getPackingTime(), booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        bid.getContainers().add(container);
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        containerRepository.save(container);
      } else {
        throw new NotFoundException(ErrorMessage.CONTAINER_NOT_SUITABLE);
      }
    });

    Double bidPrice = request.getBidPrice();
    if (bidPrice <= biddingDocument.getBidFloorPrice()) {
      throw new InternalException(ErrorMessage.BID_INVALID_BID_PRICE);
    }
    bid.setBidPrice(request.getBidPrice());

    LocalDateTime bidDate = LocalDateTime.now();
    bid.setBidDate(bidDate);

    bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    LocalDateTime validityPeriod = Tool.convertToLocalDateTime(request.getValidityPeriod());
    if (validityPeriod.isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorMessage.BID_INVALID_VALIDITY_PERIOD);
    } else if (validityPeriod.isAfter(biddingDocument.getBidClosing())) {
      bid.setValidityPeriod(biddingDocument.getBidClosing());
    } else {
      bid.setValidityPeriod(validityPeriod);
    }
    bid.setStatus(EnumBidStatus.PENDING.name());

    Bid _bid = bidRepository.save(bid);

    biddingDocument.getBids().add(bid);
    if (bid.getBidPrice() < biddingDocument.getPriceLeadership()) {
      biddingDocument.setPriceLeadership(bid.getBidPrice());
    }
    biddingDocumentRepository.save(biddingDocument);

    BiddingNotification biddingNotification = biddingNotificationRepository
        .findByUserAndBiddingDocument(username, bidDocId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND));
    biddingNotification.setIsHide(true);
    biddingNotificationRepository.save(biddingNotification);

    return _bid;
  }

  @Override
  public Bid getBid(Long id, String username) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (!(bid.getBidder().getUsername().equals(username)
        || bid.getBiddingDocument().getOfferee().getUsername().equals(username))) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    if (bid.getBiddingDocument().getBidClosing().isBefore(LocalDateTime.now()) && bid.getDateOfDecision() == null) {
      bid.setStatus(EnumBidStatus.EXPIRED.name());

    }
    return bid;
  }

  @Override
  public Page<Bid> getBidsByBiddingDocument(Long id, String username, PaginationRequest request) {
    logger.info("{} get bids from Bidding document {}", username, id);
    if (!biddingDocumentRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND);
    }
    if (!supplierRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    String status = request.getStatus();
    if (status != null) {
      return bidRepository.findByBiddingDocument(id, username, status, page);
    } else {
      return bidRepository.findByBiddingDocument(id, username, page);
    }

  }

  @Override
  public Page<Bid> getBidsByBiddingDocumentAndExistCombined(Long id, String username, PaginationRequest request) {
    if (!biddingDocumentRepository.existsById(id)) {
      throw new NotFoundException(ErrorMessage.BIDDINGDOCUMENT_NOT_FOUND);
    }
    if (!supplierRepository.existsByUsername(username)) {
      throw new NotFoundException(ErrorMessage.USER_NOT_FOUND);
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Direction.DESC, "createdAt"));
    Page<Bid> bids = bidRepository.findByBiddingDocumentAndExistCombined(id, username, page);
    return bids;
  }

  @Override
  public Page<Bid> getBidsByForwarder(String username, PaginationRequest request) {
    Page<Bid> bids = null;
    String status = request.getStatus();
    if (status != null && !status.isEmpty()) {
      bids = bidRepository.findByForwarder(username, status,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by("createdAt").descending()));
    } else {
      bids = bidRepository.findByForwarder(username,
          PageRequest.of(request.getPage(), request.getLimit(), Sort.by("createdAt").descending()));
    }
    return bids;
  }

  @Override
  public Bid editBid(Long id, String username, Map<String, Object> updates) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    Role role = user.getRoles().iterator().next();

    String bidStatus = bid.getStatus();
    if (role.getName().equalsIgnoreCase("ROLE_FORWARDER") && !(bidStatus.equalsIgnoreCase(EnumBidStatus.PENDING.name())
        || bidStatus.equalsIgnoreCase(EnumBidStatus.EXPIRED.name()))) {
      throw new InternalException(ErrorMessage.BID_INVALID_EDIT);
    }
    List<Container> containers = new ArrayList<>(bid.getContainers());

    BiddingDocument biddingDocument = bid.getBiddingDocument();

    String biddingStatus = biddingDocument.getStatus();
    if (biddingStatus.equalsIgnoreCase(EnumBiddingStatus.CANCELED.name())
        || biddingDocument.getBidClosing().isBefore(LocalDateTime.now())) {
      throw new InternalException(ErrorMessage.BIDDINGDOCUMENT_TIME_OUT);
    }

    String bidValidityPeriod = String.valueOf(updates.get("validityPeriod"));
    if (updates.get("validityPeriod") != null && !Tool.isBlank(bidValidityPeriod)) {
      LocalDateTime validityPeriod = Tool.convertToLocalDateTime(bidValidityPeriod);
      if (validityPeriod.isBefore(LocalDateTime.now())) {
        throw new InternalException(ErrorMessage.BID_INVALID_VALIDITY_PERIOD);
      } else if (validityPeriod.isAfter(biddingDocument.getBidClosing())) {
        bid.setValidityPeriod(biddingDocument.getBidClosing());
      } else {
        bid.setValidityPeriod(validityPeriod);
      }
      bid.getContainers().forEach(container -> {
        if (!container.getStatus().equals(EnumSupplyStatus.CREATED.name())) {
          throw new InternalException(ErrorMessage.CONTAINER_BUSY);
        }
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        containerRepository.save(container);
      });
      bid.setStatus(EnumBidStatus.PENDING.name());
      bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    }
    LocalDateTime freezeTime = bid.getFreezeTime();
    if (freezeTime.isAfter(LocalDateTime.now())) {
      throw new InternalException(ErrorMessage.BID_EDIT_BEFORE_FREEZE_TIME);
    }

    String bidPriceString = String.valueOf(updates.get("bidPrice"));
    if (updates.get("bidPrice") != null && !Tool.isEqual(bid.getBidPrice(), bidPriceString)) {
      bid.setBidPrice(Double.parseDouble(bidPriceString));
      bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    }

    String statusString = String.valueOf(updates.get("status"));
    if (updates.get("status") != null && !Tool.isEqual(bid.getStatus(), statusString)) {
      EnumBidStatus status = EnumBidStatus.findByName(statusString);
      bid.setStatus(status.name());

      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
        bid.setDateOfDecision(LocalDateTime.now());
        containers.forEach(container -> {
          container.setStatus(EnumSupplyStatus.CREATED.name());
          containerRepository.save(container);
        });
      }

      if (bid.getStatus().equalsIgnoreCase(EnumBidStatus.CANCELED.name())
          || bid.getStatus().equalsIgnoreCase(EnumBidStatus.EXPIRED.name())) {
        containers.forEach(container -> {
          container.setStatus(EnumSupplyStatus.CREATED.name());
          containerRepository.save(container);
        });
      }
    }

    Bid _bid = bidRepository.save(bid);

    return _bid;
  }

  @Override
  public Bid editBidWhenCombined(Long id, String username, List<Long> containersId) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    Role role = user.getRoles().iterator().next();
    if (!role.getName().equalsIgnoreCase("ROLE_MERCHANT")) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    bid.setDateOfDecision(LocalDateTime.now());
    bid.setStatus(EnumBidStatus.ACCEPTED.name());

    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    Long combinedContainers = containerRepository.countCombinedContainersByBiddingDocument(biddingDocument.getId());

    if (containersId.size() < 1) {
      throw new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND);
    }
    if (booking.getUnit() < combinedContainers + containersId.size()) {
      throw new InternalException(ErrorMessage.CONTAINER_MORE_THAN_NEEDED);
    }
    if (booking.getUnit() == combinedContainers + containersId.size()) {
      biddingDocument.setStatus(EnumBiddingStatus.COMBINED.name());
      biddingDocumentRepository.save(biddingDocument);
      outbound.setStatus(EnumSupplyStatus.COMBINED.name());
      outboundRepository.save(outbound);
      biddingDocument.getBids().forEach(item -> {
        if (!item.getStatus().equals(EnumBidStatus.ACCEPTED.name()) && item.getId() != bid.getId()) {
          editExpiredBids(bid, EnumBidStatus.REJECTED.name());
        }
      });
    }
    containersId.forEach(containerId -> {
      if (containerRepository.isContainedByBid(containerId, id)) {
        Container container = containerRepository.findById(containerId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
        container.setStatus(EnumSupplyStatus.COMBINED.name());
        containerRepository.save(container);
      }
    });

    Bid _bid = bidRepository.save(bid);

    return _bid;
  }

  @Override
  public void removeBid(Long id, String username) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (bid.getBidder().getUsername() != username) {
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
      throw new NotFoundException(ErrorMessage.BID_NOT_FOUND);
    }
  }

  @Override
  public Bid replaceContainer(Long id, String username, ReplaceContainerRequest request) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (!bid.getBidder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    BiddingDocument biddingDocument = bid.getBiddingDocument();
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    Long oldContId = request.getOldContainerId();
    Long newContId = request.getNewContainerId();
    Container oldContainer = containerRepository.findById(oldContId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
    Container newContainer = containerRepository.findById(newContId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
    while (bid.getContainers().contains(oldContainer)) {
      bid.getContainers().remove(oldContainer);
    }
    oldContainer.getBids().remove(bid);
    oldContainer.setStatus(EnumSupplyStatus.CREATED.name());
    containerRepository.save(oldContainer);

    if (containerRepository.existsByOutbound(newContId, outbound.getShippingLine().getCompanyCode(),
        outbound.getContainerType().getName(), Arrays.asList(EnumSupplyStatus.CREATED.name()),
        outbound.getPackingTime(), booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
      bid.getContainers().add(newContainer);
      newContainer.setStatus(EnumSupplyStatus.BIDDING.name());
      containerRepository.save(newContainer);
    } else {
      throw new NotFoundException(ErrorMessage.CONTAINER_NOT_SUITABLE);
    }

    bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    Bid _bid = bidRepository.save(bid);
    return _bid;
  }

  @Override
  public Bid addContainer(Long id, String username, BidRequest request) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (!bid.getBidder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (!bid.getStatus().equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
      throw new InternalException(ErrorMessage.BID_INVALID_EDIT);
    }

    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (biddingDocument.getIsMultipleAward() == false) {
      throw new InternalException(ErrorMessage.CONTAINER_CAN_NOT_ADD_OR_REMOVE);
    }
    Outbound outbound = biddingDocument.getOutbound();
    Booking booking = outbound.getBooking();
    List<Long> containers = request.getContainers();
    if (bid.getContainers().size() >= booking.getUnit()) {
      throw new InternalException(ErrorMessage.CONTAINER_MORE_THAN_NEEDED);
    }
    containers.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
      if (containerRepository.existsByOutbound(containerId, outbound.getShippingLine().getCompanyCode(),
          outbound.getContainerType().getName(), Arrays.asList(EnumSupplyStatus.CREATED.name()),
          outbound.getPackingTime(), booking.getCutOffTime(), booking.getPortOfLoading().getNameCode())) {
        container.setStatus(EnumSupplyStatus.BIDDING.name());
        bid.getContainers().add(container);
        containerRepository.save(container);
      } else {
        throw new NotFoundException(ErrorMessage.CONTAINER_NOT_SUITABLE + ": " + container.getNumber());
      }
    });

    bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    Bid _bid = bidRepository.save(bid);
    return _bid;
  }

  @Override
  public Bid removeContainer(Long id, String username, Long containerId) {
    Bid bid = bidRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (!bid.getBidder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }

    if (!bid.getStatus().equalsIgnoreCase(EnumBidStatus.PENDING.name())) {
      throw new InternalException(ErrorMessage.BID_INVALID_EDIT);
    }

    BiddingDocument biddingDocument = bid.getBiddingDocument();
    if (biddingDocument.getIsMultipleAward() == false) {
      throw new InternalException(ErrorMessage.CONTAINER_CAN_NOT_ADD_OR_REMOVE);
    }
    Container container = containerRepository.findById(containerId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
    if (bid.getContainers().size() == 1) {
      throw new InternalException(ErrorMessage.CONTAINER_CAN_NOT_BE_ZERO);
    }
    if (!bid.getContainers().contains(container)) {
      throw new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND);
    }
    container.setStatus(EnumSupplyStatus.CREATED.name());
    container.getBids().remove(bid);
    containerRepository.save(container);

    while (bid.getContainers().contains(container)) {
      bid.getContainers().remove(container);
    }
    bid.setFreezeTime(LocalDateTime.now().plusMinutes(Constant.FREEZE_TIME));
    Bid _bid = bidRepository.save(bid);
    return _bid;
  }

  @Override
  public void editExpiredBids(Bid bid, String status) {
    Collection<Container> containers = bid.getContainers();
    bid.setStatus(status);
    if (status.equalsIgnoreCase(EnumBidStatus.REJECTED.name())) {
      bid.setDateOfDecision(LocalDateTime.now());
      containers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });
    }

    if (status.equalsIgnoreCase(EnumBidStatus.CANCELED.name())
        || status.equalsIgnoreCase(EnumBidStatus.EXPIRED.name())) {
      containers.forEach(container -> {
        container.setStatus(EnumSupplyStatus.CREATED.name());
        containerRepository.save(container);
      });
    }

    bidRepository.save(bid);

  }

  @Override
  public List<Bid> updateExpiredBidFromList(List<Bid> bids) {
    List<Bid> result = new ArrayList<>();
    for (Bid bid : bids) {
      BiddingDocument biddingDocument = bid.getBiddingDocument();
      String status = biddingDocument.getStatus();
      boolean existsCombinedBid = biddingDocumentRepository.existsCombinedBid(biddingDocument.getId());
      if (biddingDocument.getBidClosing().isBefore(LocalDateTime.now())
          && biddingDocument.getStatus().equals(EnumBiddingStatus.BIDDING.name())) {
        if (!existsCombinedBid) {
          status = EnumBiddingStatus.EXPIRED.name();
          bid.setStatus(EnumBidStatus.EXPIRED.name());
        } else if (existsCombinedBid) {
          status = EnumBiddingStatus.COMBINED.name();
          if (bid.getStatus().equals(EnumBidStatus.PENDING.name())) {
            bid.setStatus(EnumBidStatus.EXPIRED.name());
          }
        }
        biddingDocumentService.updateExpiredBiddingDocuments(biddingDocument.getId(), status);
      }
      if (bid.getValidityPeriod().isBefore(LocalDateTime.now())
          && bid.getStatus().equals(EnumBidStatus.PENDING.name())) {
        bid.setStatus(EnumBidStatus.EXPIRED.name());
        editExpiredBids(bid, EnumBidStatus.EXPIRED.name());
      }
      result.add(bid);
    }
    return result;
  }

}

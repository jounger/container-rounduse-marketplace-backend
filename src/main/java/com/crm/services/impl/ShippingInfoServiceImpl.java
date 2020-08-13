package com.crm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.crm.common.ErrorMessage;
import com.crm.enums.EnumShippingStatus;
import com.crm.exception.ForbiddenException;
import com.crm.exception.NotFoundException;
import com.crm.models.Bid;
import com.crm.models.Combined;
import com.crm.models.Container;
import com.crm.models.Driver;
import com.crm.models.Forwarder;
import com.crm.models.Merchant;
import com.crm.models.Outbound;
import com.crm.models.ShippingInfo;
import com.crm.models.User;
import com.crm.payload.request.PaginationRequest;
import com.crm.payload.request.ShippingInfoRequest;
import com.crm.repository.BidRepository;
import com.crm.repository.CombinedRepository;
import com.crm.repository.ContainerRepository;
import com.crm.repository.OutboundRepository;
import com.crm.repository.ShippingInfoRepository;
import com.crm.repository.UserRepository;
import com.crm.services.ShippingInfoService;

@Service
public class ShippingInfoServiceImpl implements ShippingInfoService {

  @Autowired
  private ShippingInfoRepository shippingInfoRepository;

  @Autowired
  private CombinedRepository combinedRepository;

  @Autowired
  private OutboundRepository outboundRepository;

  @Autowired
  private ContainerRepository containerRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public ShippingInfo createShippingInfo(ShippingInfoRequest request) {
    ShippingInfo shippingInfo = new ShippingInfo();
    Combined combined = combinedRepository.findById(request.getCombined())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    shippingInfo.setCombined(combined);
    Outbound outbound = outboundRepository.findById(request.getOutbound())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    shippingInfo.setOutbound(outbound);
    Container container = containerRepository.findById(request.getContainer())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND_IN_BID));
    shippingInfo.setContainer(container);
    shippingInfo.setStatus(EnumShippingStatus.INFO_RECEIVED.name());

    ShippingInfo _shippingInfo = shippingInfoRepository.save(shippingInfo);
    return _shippingInfo;
  }

  @Override
  public void createShippingInfosForCombined(Combined combined, List<Long> containers) {
    ShippingInfoRequest shippingInfoRequest = new ShippingInfoRequest();
    shippingInfoRequest.setCombined(combined.getId());
    Outbound outbound = outboundRepository.findByCombined(combined.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    shippingInfoRequest.setOutbound(outbound.getId());
    shippingInfoRequest.setStatus(EnumShippingStatus.INFO_RECEIVED.name());
    containers.forEach(containerId -> {
      Container container = containerRepository.findById(containerId)
          .orElseThrow(() -> new NotFoundException(ErrorMessage.CONTAINER_NOT_FOUND));
      shippingInfoRequest.setContainer(container.getId());
      ShippingInfo shippingInfo = createShippingInfo(shippingInfoRequest);
      combined.getShippingInfos().add(shippingInfo);
    });

  }

  @Override
  public ShippingInfo getShippingInfo(Long id, String username) {
    ShippingInfo shippingInfo = shippingInfoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPING_INFO_NOT_FOUND));
    Container container = shippingInfo.getContainer();
    Driver driver = container.getDriver();
    Forwarder forwarder = driver.getForwarder();
    Outbound outbound = shippingInfo.getOutbound();
    Merchant merchant = outbound.getMerchant();
    if (!(driver.getUsername().equals(username) || !merchant.getUsername().equals(username)
        || forwarder.getUsername().equals(username))) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    return shippingInfo;
  }

  @Override
  public Page<ShippingInfo> getShippingInfosByBid(Long bidId, String username, PaginationRequest request) {
    Bid bid = bidRepository.findById(bidId).orElseThrow(() -> new NotFoundException(ErrorMessage.BID_NOT_FOUND));
    if (bid.getBidder().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ShippingInfo> pages = shippingInfoRepository.findByDriver(username, page);
    return pages;
  }

  @Override
  public Page<ShippingInfo> getShippingInfosByOutbound(Long outboundId, String username, PaginationRequest request) {
    Outbound outbound = outboundRepository.findById(outboundId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.OUTBOUND_NOT_FOUND));
    if (!outbound.getMerchant().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ShippingInfo> pages = shippingInfoRepository.findByOutbound(outboundId, page);
    return pages;
  }

  @Override
  public Page<ShippingInfo> getShippingInfosByDriver(String username, PaginationRequest request) {
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ShippingInfo> pages = shippingInfoRepository.findByDriver(username, page);
    return pages;
  }

  @Override
  public ShippingInfo editShippingInfo(Long id, String username, String status) {
    ShippingInfo shippingInfo = shippingInfoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.SHIPPING_INFO_NOT_FOUND));
    Container container = shippingInfo.getContainer();
    if (!container.getDriver().getUsername().equals(username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    EnumShippingStatus eStatus = EnumShippingStatus.findByName(status);
    if (eStatus == null) {
      throw new NotFoundException(ErrorMessage.SHIPPING_INFO_STATUS_NOT_FOUND);
    }
    shippingInfo.setStatus(eStatus.name());

    ShippingInfo _shippingInfo = shippingInfoRepository.save(shippingInfo);
    return _shippingInfo;
  }

  @Override
  public void removeShippingInfo(Long id, String username) {
    if (!shippingInfoRepository.isForwarder(id, username)) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    shippingInfoRepository.deleteById(id);
  }

  @Override
  public Page<ShippingInfo> getShippingInfosByCombined(Long combinedId, String username, PaginationRequest request) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    String role = user.getRoles().iterator().next().getName();
    Combined combined = combinedRepository.findById(combinedId)
        .orElseThrow(() -> new NotFoundException(ErrorMessage.COMBINED_NOT_FOUND));
    if (!(combined.getBid().getBidder().getUsername().equals(username)
        || combined.getBid().getBiddingDocument().getOfferee().getUsername().equals(username)
        || role.equalsIgnoreCase("ROLE_SHIPPINGLINE"))) {
      throw new ForbiddenException(ErrorMessage.USER_ACCESS_DENIED);
    }
    PageRequest page = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<ShippingInfo> pages = shippingInfoRepository.findByCombined(combinedId, page);
    return pages;
  }

}

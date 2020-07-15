package com.crm.models.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.crm.common.Tool;
import com.crm.models.Bid;
import com.crm.models.BiddingNotification;
import com.crm.models.Container;
import com.crm.models.dto.BidDto;
import com.crm.models.dto.ContainerDto;
import com.crm.models.dto.ShippingLineNotificationDto;

public class ShippingLineNotificationMapper {

  public static ShippingLineNotificationDto toShippingLineNotificationDto(BiddingNotification biddingNotification,
      Bid bid) {
    ShippingLineNotificationDto shippingLineNotificationDto = new ShippingLineNotificationDto();

    BidDto bidDto = BidMapper.toBidDto(bid);
    String merchant = biddingNotification.getRelatedResource().getOfferee().getUsername();
    String forwarder = bidDto.getBidder();
    Set<Container> setContainer = new HashSet<>(bid.getContainers());
    List<ContainerDto> containersdto = new ArrayList<>();
    setContainer.forEach(container -> {
      ContainerDto containerDto = ContainerMapper.toContainerDto(container);
      containersdto.add(containerDto);
    });
    shippingLineNotificationDto.setMerchant(merchant);
    shippingLineNotificationDto.setForwarder(forwarder);
    shippingLineNotificationDto.setContainers(containersdto);
    shippingLineNotificationDto.setId(biddingNotification.getId());
    shippingLineNotificationDto.setRecipient(biddingNotification.getRecipient().getUsername());
    shippingLineNotificationDto.setIsRead(biddingNotification.getIsRead());
    shippingLineNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(biddingNotification.getSendDate()));

    return shippingLineNotificationDto;
  }
}

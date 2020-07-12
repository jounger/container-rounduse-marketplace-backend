package com.crm.websocket.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.Bid;
import com.crm.models.BiddingNotification;
import com.crm.models.DriverNotification;
import com.crm.models.dto.BiddingNotificationDto;
import com.crm.models.dto.DriverNotificationDto;
import com.crm.models.dto.ShippingLineNotificationDto;
import com.crm.models.mapper.BiddingNotificationMapper;
import com.crm.models.mapper.DriverNotificationMapper;
import com.crm.models.mapper.ShippingLineNotificationMapper;
import com.crm.websocket.service.BiddingWebSocketService;

@Service
public class BiddingWebSocketServiceImpl implements BiddingWebSocketService {

  private static final Logger logger = LoggerFactory.getLogger(BiddingWebSocketServiceImpl.class);

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendBiddingNotifyToUser(BiddingNotification notification) {
    BiddingNotificationDto notificationDto = BiddingNotificationMapper.toBiddingNotificationDto(notification);
    logger.info("Send to: {}", notificationDto.getRecipient());
    logger.info("Notification: {}", notificationDto.toString());
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient(), Constant.BIDDING_NOTIFICATION,
        notificationDto);
  }

  @Override
  public void sendBiddingNotifyToShippingLine(BiddingNotification notification, Bid bid) {
    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper
        .toShippingLineNotificationDto(notification, bid);
    logger.info("Send to: {}", shippingLineNotificationDto.getRecipient());
    logger.info("shippingLineNotification: {}", shippingLineNotificationDto.toString());
    messagingTemplate.convertAndSendToUser(shippingLineNotificationDto.getRecipient(), Constant.BIDDING_NOTIFICATION,
        shippingLineNotificationDto);
  }

  @Override
  public void sendBiddingNotifyToDriver(DriverNotification notification) {
    DriverNotificationDto driverNotificationDto = DriverNotificationMapper.toDriverNotificationDto(notification);
    logger.info("Send to: {}", driverNotificationDto.getRecipient());
    logger.info("shippingLineNotification: {}", driverNotificationDto.toString());
    messagingTemplate.convertAndSendToUser(driverNotificationDto.getRecipient(), Constant.BIDDING_NOTIFICATION,
        driverNotificationDto);
  }
}

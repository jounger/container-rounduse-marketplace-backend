package com.crm.websocket.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.dto.BiddingNotificationDto;
import com.crm.websocket.service.BiddingWebSocketService;

@Service
public class BiddingWebSocketServiceImpl implements BiddingWebSocketService {

  private static final Logger logger = LoggerFactory.getLogger(BiddingWebSocketServiceImpl.class);

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendBiddingNotifyToUser(BiddingNotificationDto notificationDto) {
    logger.info("Send to: {}", notificationDto.getRecipient());
    logger.info("Notification: {}", notificationDto.toString());
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient().getUsername(), Constant.BIDDING_NOTIFICATION,
        notificationDto);
  }
}

package com.crm.websocket.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.BiddingNotification;
import com.crm.models.dto.BiddingNotificationDto;
import com.crm.models.mapper.BiddingNotificationMapper;
import com.crm.websocket.service.EvidenceWebSocketService;

@Service
public class EvidenceWebSocketServiceImpl implements EvidenceWebSocketService {

  private static final Logger logger = LoggerFactory.getLogger(EvidenceWebSocketServiceImpl.class);

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendEvidenceNotifyToUser(BiddingNotification notification) {
    BiddingNotificationDto notificationDto = BiddingNotificationMapper.toBiddingNotificationDto(notification);
    logger.info("Send to: {}", notificationDto.getRecipient());
    logger.info("Notification: {}", notificationDto.toString());
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient().getUsername(), Constant.BIDDING_NOTIFICATION,
        notificationDto);
  }

}

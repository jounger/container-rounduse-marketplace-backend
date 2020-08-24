package com.crm.websocket.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.CombinedNotification;
import com.crm.models.dto.CombinedNotificationDto;
import com.crm.models.mapper.CombinedNotificationMapper;
import com.crm.websocket.service.ContractDocumentWebSocketService;

@Service
public class ContractDocumentWebSocketServiceImpl implements ContractDocumentWebSocketService {

  private static final Logger logger = LoggerFactory.getLogger(ContractDocumentWebSocketServiceImpl.class);

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendContractDocumentNotifyToUser(CombinedNotification notification) {
    CombinedNotificationDto notificationDto = CombinedNotificationMapper.toCombinedNotificationDto(notification);
    logger.info("Send to: {}", notificationDto.getRecipient());
    logger.info("Notification: {}", notificationDto.toString());
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient().getUsername(), Constant.BIDDING_NOTIFICATION,
        notificationDto);
  }

}

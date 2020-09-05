package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.dto.CombinedNotificationDto;
import com.crm.websocket.service.CombinedWebSocketService;

@Service
public class CombinedWebsocketServiceImpl implements CombinedWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendCombinedNotifyToShippingLine(CombinedNotificationDto notification) {
    messagingTemplate.convertAndSendToUser(notification.getRecipient().getUsername(),
        Constant.SHIPPING_LINE_NOTIFICATION, notification);
  }

}

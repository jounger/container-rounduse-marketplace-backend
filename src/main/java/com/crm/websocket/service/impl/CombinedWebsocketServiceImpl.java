package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.CombinedNotification;
import com.crm.models.dto.CombinedNotificationDto;
import com.crm.models.mapper.CombinedNotificationMapper;
import com.crm.websocket.service.CombinedWebSocketService;

@Service
public class CombinedWebsocketServiceImpl implements CombinedWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendCombinedNotifyToShippingLine(CombinedNotification notification) {

    CombinedNotificationDto shippingLineNotificationDto = CombinedNotificationMapper
        .toCombinedNotificationDto(notification);
    messagingTemplate.convertAndSendToUser(shippingLineNotificationDto.getRecipient().getUsername(),
        Constant.SHIPPING_LINE_NOTIFICATION, shippingLineNotificationDto);
  }

}

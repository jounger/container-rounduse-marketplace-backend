package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.ShippingLineNotification;
import com.crm.models.dto.ShippingLineNotificationDto;
import com.crm.models.mapper.ShippingLineNotificationMapper;
import com.crm.websocket.service.ShippingLineWebSocketService;

@Service
public class ShippingLineWebsocketServiceImpl implements ShippingLineWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendCombinedNotifyToShippingLine(ShippingLineNotification notification) {

    ShippingLineNotificationDto shippingLineNotificationDto = ShippingLineNotificationMapper
        .toShippingLineNotificationDto(notification);
    messagingTemplate.convertAndSendToUser(shippingLineNotificationDto.getRecipient().getUsername(),
        Constant.SHIPPING_LINE_NOTIFICATION, shippingLineNotificationDto);
  }

}

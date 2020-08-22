package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.ShippingNotification;
import com.crm.models.dto.ShippingNotificationDto;
import com.crm.models.mapper.ShippingNotificationMapper;
import com.crm.websocket.service.ShippingWebSocketService;

@Service
public class ShippingWebSocketServiceImpl implements ShippingWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendBiddingNotifyToDriver(ShippingNotification notification) {
    ShippingNotificationDto driverNotificationDto = ShippingNotificationMapper.toDriverNotificationDto(notification);
    messagingTemplate.convertAndSendToUser(driverNotificationDto.getRecipient().getUsername(),
        Constant.DRIVER_NOTIFICATION, driverNotificationDto);
  }
}

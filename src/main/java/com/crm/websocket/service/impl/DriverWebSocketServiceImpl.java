package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.DriverNotification;
import com.crm.models.dto.DriverNotificationDto;
import com.crm.models.mapper.DriverNotificationMapper;
import com.crm.websocket.service.DriverWebSocketService;

@Service
public class DriverWebSocketServiceImpl implements DriverWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendBiddingNotifyToDriver(DriverNotification notification) {
    DriverNotificationDto driverNotificationDto = DriverNotificationMapper.toDriverNotificationDto(notification);
    messagingTemplate.convertAndSendToUser(driverNotificationDto.getRecipient().getUsername(),
        Constant.DRIVER_NOTIFICATION, driverNotificationDto);
  }
}

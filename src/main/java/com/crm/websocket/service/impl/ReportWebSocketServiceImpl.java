package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.dto.ReportNotificationDto;
import com.crm.websocket.service.ReportWebSocketService;

@Service
public class ReportWebSocketServiceImpl implements ReportWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendReportNotifyToModeratorOrUser(ReportNotificationDto notificationDto) {
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient().getUsername(), Constant.REPORT_NOTIFICATION,
        notificationDto);
  }

}

package com.crm.websocket.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.ReportNotification;
import com.crm.models.dto.ReportNotificationDto;
import com.crm.models.mapper.ReportNotificationMapper;
import com.crm.websocket.service.ReportWebSocketService;

@Service
public class ReportWebSocketServiceImpl implements ReportWebSocketService {

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendReportNotifyToModeratorOrUser(ReportNotification notification) {
    ReportNotificationDto notificationDto = ReportNotificationMapper.toReportNotificationDto(notification);
    messagingTemplate.convertAndSendToUser(notificationDto.getRecipient(), Constant.REPORT_NOTIFICATION,
        notificationDto);
  }

}

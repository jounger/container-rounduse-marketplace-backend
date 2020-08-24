package com.crm.websocket.service;

import com.crm.models.dto.ReportNotificationDto;

public interface ReportWebSocketService {

  void sendReportNotifyToModeratorOrUser(ReportNotificationDto notification);
}

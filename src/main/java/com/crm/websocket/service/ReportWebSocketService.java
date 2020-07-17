package com.crm.websocket.service;

import com.crm.models.ReportNotification;

public interface ReportWebSocketService {

  void sendReportNotifyToModeratorOrUser(ReportNotification notification);
}

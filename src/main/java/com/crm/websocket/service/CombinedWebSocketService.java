package com.crm.websocket.service;

import com.crm.models.dto.CombinedNotificationDto;

public interface CombinedWebSocketService {

  void sendCombinedNotifyToShippingLine(CombinedNotificationDto notification);
}

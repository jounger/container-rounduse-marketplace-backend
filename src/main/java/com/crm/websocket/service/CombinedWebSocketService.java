package com.crm.websocket.service;

import com.crm.models.CombinedNotification;

public interface CombinedWebSocketService {

  void sendCombinedNotifyToShippingLine(CombinedNotification notification);
}

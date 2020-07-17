package com.crm.websocket.service;

import com.crm.models.ShippingLineNotification;

public interface ShippingLineWebSocketService {

  void sendCombinedNotifyToShippingLine(ShippingLineNotification notification);
}

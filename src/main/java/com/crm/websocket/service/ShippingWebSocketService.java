package com.crm.websocket.service;

import com.crm.models.dto.ShippingNotificationDto;

public interface ShippingWebSocketService {

  void sendBiddingNotifyToDriver(ShippingNotificationDto notification);
}

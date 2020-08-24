package com.crm.websocket.service;

import com.crm.models.ShippingNotification;

public interface ShippingWebSocketService {

  void sendBiddingNotifyToDriver(ShippingNotification notification);
}

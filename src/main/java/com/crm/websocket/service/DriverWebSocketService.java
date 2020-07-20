package com.crm.websocket.service;

import com.crm.models.DriverNotification;

public interface DriverWebSocketService {

  void sendBiddingNotifyToDriver(DriverNotification notification);
}

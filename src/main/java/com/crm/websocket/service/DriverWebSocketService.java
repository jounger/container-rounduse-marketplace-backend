package com.crm.websocket.service;

import com.crm.models.DriverNotification;

public interface DriverWebSocketService {

  public void sendBiddingNotifyToDriver(DriverNotification notification);
}

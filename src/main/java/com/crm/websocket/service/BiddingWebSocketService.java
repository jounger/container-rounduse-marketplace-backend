package com.crm.websocket.service;

import com.crm.models.Bid;
import com.crm.models.BiddingNotification;
import com.crm.models.DriverNotification;

public interface BiddingWebSocketService {

  void sendBiddingNotifyToUser(BiddingNotification notification);

  void sendBiddingNotifyToShippingLine(BiddingNotification notification, Bid bid);

  void sendBiddingNotifyToDriver(DriverNotification notification);
}

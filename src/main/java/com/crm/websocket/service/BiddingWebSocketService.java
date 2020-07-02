package com.crm.websocket.service;

import com.crm.models.Bid;
import com.crm.models.BiddingNotification;

public interface BiddingWebSocketService {

  void broadcastBiddingNotifyToUser(BiddingNotification notification);

  void broadcastBiddingNotifyToShippingLine(BiddingNotification notification, Bid bid);
}

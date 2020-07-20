package com.crm.websocket.service;

import com.crm.models.BiddingNotification;

public interface BiddingWebSocketService {

  void sendBiddingNotifyToUser(BiddingNotification notification);
}

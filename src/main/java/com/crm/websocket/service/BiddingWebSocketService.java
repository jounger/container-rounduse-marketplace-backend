package com.crm.websocket.service;

import com.crm.models.dto.BiddingNotificationDto;

public interface BiddingWebSocketService {

  void sendBiddingNotifyToUser(BiddingNotificationDto notification);
}

package com.crm.websocket.service;

import com.crm.models.BiddingNotification;

public interface EvidenceWebSocketService {

  void sendEvidenceNotifyToUser(BiddingNotification notification);
}

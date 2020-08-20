package com.crm.websocket.service;

import com.crm.models.BiddingNotification;

public interface ContractDocumentWebSocketService {

  void sendContractDocumentNotifyToUser(BiddingNotification notification);
}

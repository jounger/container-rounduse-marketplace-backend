package com.crm.websocket.service;

import com.crm.models.CombinedNotification;

public interface ContractDocumentWebSocketService {

  void sendContractDocumentNotifyToUser(CombinedNotification notification);
}

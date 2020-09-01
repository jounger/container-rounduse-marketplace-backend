package com.crm.websocket.service;

import com.crm.models.dto.CombinedNotificationDto;

public interface ContractDocumentWebSocketService {

  void sendContractDocumentNotifyToUser(CombinedNotificationDto notification);
}

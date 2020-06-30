package com.crm.websocket.service;

import com.crm.models.BiddingDocument;
import com.crm.models.User;

public interface BiddingNotifyService {

  void BiddingDocumentNotification(BiddingDocument biddingDocument, User users);
}

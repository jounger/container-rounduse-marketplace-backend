package com.crm.websocket.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.crm.common.Constant;
import com.crm.models.BiddingDocument;
import com.crm.models.User;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.mapper.BiddingDocumentMapper;
import com.crm.websocket.service.BiddingNotifyService;

@Service
public class BiddingNotifyServiceImpl implements BiddingNotifyService {

  private static final Logger logger = LoggerFactory.getLogger(BiddingNotifyServiceImpl.class);

  @Autowired
  SimpMessagingTemplate messagingTemplate;

  @Override
  public void BiddingDocumentNotification(BiddingDocument biddingDocument, User users) {
    BiddingDocumentDto biddingDocumentDto = BiddingDocumentMapper.toBiddingDocumentDto(biddingDocument);
    messagingTemplate.convertAndSendToUser(users.getUsername() , Constant.BIDDING_NOTIFICATION , biddingDocumentDto);
  }
}

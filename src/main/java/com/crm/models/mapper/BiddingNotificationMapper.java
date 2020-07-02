package com.crm.models.mapper;


import com.crm.common.Tool;
import com.crm.models.BiddingNotification;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.BiddingNotificationDto;

public class BiddingNotificationMapper {

  public static BiddingNotificationDto toBiddingNotificationDto(BiddingNotification biddingNotification) {
    BiddingNotificationDto biddingNotificationDto = new BiddingNotificationDto();
    
    biddingNotificationDto.setId(biddingNotification.getId());
    biddingNotificationDto.setRecipient(biddingNotification.getRecipient().getUsername());
    biddingNotificationDto.setIsRead(biddingNotification.getIsRead());
    
    BiddingDocumentDto relatedResource = BiddingDocumentMapper.toBiddingDocumentDto(biddingNotification.getRelatedResource());
    biddingNotificationDto.setRelatedResource(relatedResource);
    
    biddingNotificationDto.setMessage(biddingNotification.getMessage());
    biddingNotificationDto.setType(biddingNotification.getType());
    
    biddingNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(biddingNotification.getSendDate()));
    
    return biddingNotificationDto;
  }
}

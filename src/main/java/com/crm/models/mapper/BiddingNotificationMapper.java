package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.BiddingNotification;
import com.crm.models.dto.BiddingDocumentDto;
import com.crm.models.dto.BiddingNotificationDto;

public class BiddingNotificationMapper {

  public static BiddingNotificationDto toBiddingNotificationDto(BiddingNotification biddingNotification) {
    if (biddingNotification == null) {
      return null;
    }

    BiddingNotificationDto biddingNotificationDto = new BiddingNotificationDto();

    biddingNotificationDto.setId(biddingNotification.getId());
    biddingNotificationDto.setRecipient(UserMapper.toUserDto(biddingNotification.getRecipient()));
    biddingNotificationDto.setIsRead(biddingNotification.getIsRead());
    biddingNotificationDto.setIsHide(biddingNotification.getIsHide());

    BiddingDocumentDto relatedResource = BiddingDocumentMapper
        .toBiddingDocumentDto(biddingNotification.getRelatedResource());
    biddingNotificationDto.setRelatedResource(relatedResource);

    biddingNotificationDto.setMessage(biddingNotification.getMessage());
    biddingNotificationDto.setAction(biddingNotification.getAction());
    biddingNotificationDto.setType(biddingNotification.getType());

    biddingNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(biddingNotification.getSendDate()));

    return biddingNotificationDto;
  }
}

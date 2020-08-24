package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.CombinedNotification;
import com.crm.models.dto.CombinedDto;
import com.crm.models.dto.CombinedNotificationDto;

public class CombinedNotificationMapper {

  public static CombinedNotificationDto toCombinedNotificationDto(
      CombinedNotification combinedNotification) {
    if (combinedNotification == null) {
      return null;
    }

    CombinedNotificationDto combinedNotificationDto = new CombinedNotificationDto();

    combinedNotificationDto.setId(combinedNotification.getId());
    combinedNotificationDto.setRecipient(UserMapper.toUserDto(combinedNotification.getRecipient()));
    combinedNotificationDto.setIsRead(combinedNotification.getIsRead());
    combinedNotificationDto.setIsHide(combinedNotificationDto.getIsHide());

    CombinedDto relatedResource = CombinedMapper.toCombinedDto(combinedNotification.getRelatedResource());
    combinedNotificationDto.setRelatedResource(relatedResource);

    combinedNotificationDto.setMessage(combinedNotification.getMessage());
    combinedNotificationDto.setAction(combinedNotification.getAction());
    combinedNotificationDto.setType(combinedNotification.getType());

    combinedNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(combinedNotification.getSendDate()));

    return combinedNotificationDto;
  }
}

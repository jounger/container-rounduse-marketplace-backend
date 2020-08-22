package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.CombinedNotification;
import com.crm.models.dto.CombinedDto;
import com.crm.models.dto.CombinedNotificationDto;

public class CombinedNotificationMapper {

  public static CombinedNotificationDto toShippingLineNotificationDto(
      CombinedNotification shippingLineNotification) {
    if (shippingLineNotification == null) {
      return null;
    }

    CombinedNotificationDto shippingLineNotificationDto = new CombinedNotificationDto();

    shippingLineNotificationDto.setId(shippingLineNotification.getId());
    shippingLineNotificationDto.setRecipient(UserMapper.toUserDto(shippingLineNotification.getRecipient()));
    shippingLineNotificationDto.setIsRead(shippingLineNotification.getIsRead());
    shippingLineNotificationDto.setIsHide(shippingLineNotificationDto.getIsHide());

    CombinedDto relatedResource = CombinedMapper.toCombinedDto(shippingLineNotification.getRelatedResource());
    shippingLineNotificationDto.setRelatedResource(relatedResource);

    shippingLineNotificationDto.setMessage(shippingLineNotification.getMessage());
    shippingLineNotificationDto.setAction(shippingLineNotification.getAction());
    shippingLineNotificationDto.setType(shippingLineNotification.getType());

    shippingLineNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(shippingLineNotification.getSendDate()));

    return shippingLineNotificationDto;
  }
}

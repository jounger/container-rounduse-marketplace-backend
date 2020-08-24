package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.ShippingNotification;
import com.crm.models.dto.ShippingInfoDto;
import com.crm.models.dto.ShippingNotificationDto;

public class ShippingNotificationMapper {

  public static ShippingNotificationDto toShippingNotificationDto(ShippingNotification shippingNotification) {
    if (shippingNotification == null) {
      return null;
    }

    ShippingNotificationDto shippingNotificationDto = new ShippingNotificationDto();

    shippingNotificationDto.setId(shippingNotification.getId());
    shippingNotificationDto.setRecipient(UserMapper.toUserDto(shippingNotification.getRecipient()));
    shippingNotificationDto.setIsRead(shippingNotification.getIsRead());
    shippingNotificationDto.setIsHide(shippingNotificationDto.getIsHide());

    ShippingInfoDto relatedResource = ShippingInfoMapper.toShippingInfoDto(shippingNotification.getRelatedResource());
    shippingNotificationDto.setRelatedResource(relatedResource);

    shippingNotificationDto.setMessage(shippingNotification.getMessage());
    shippingNotificationDto.setAction(shippingNotification.getAction());
    shippingNotificationDto.setType(shippingNotification.getType());

    shippingNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(shippingNotification.getSendDate()));

    return shippingNotificationDto;
  }
}

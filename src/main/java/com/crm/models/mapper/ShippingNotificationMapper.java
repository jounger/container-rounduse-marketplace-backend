package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.ShippingNotification;
import com.crm.models.dto.ShippingNotificationDto;
import com.crm.models.dto.ShippingInfoDto;

public class ShippingNotificationMapper {

  public static ShippingNotificationDto toDriverNotificationDto(ShippingNotification driverNotification) {
    if (driverNotification == null) {
      return null;
    }

    ShippingNotificationDto driverNotificationDto = new ShippingNotificationDto();

    driverNotificationDto.setId(driverNotification.getId());
    driverNotificationDto.setRecipient(UserMapper.toUserDto(driverNotification.getRecipient()));
    driverNotificationDto.setIsRead(driverNotification.getIsRead());
    driverNotificationDto.setIsHide(driverNotificationDto.getIsHide());

    ShippingInfoDto relatedResource = ShippingInfoMapper.toShippingInfoDto(driverNotification.getRelatedResource());
    driverNotificationDto.setRelatedResource(relatedResource);

    driverNotificationDto.setMessage(driverNotification.getMessage());
    driverNotificationDto.setAction(driverNotification.getAction());
    driverNotificationDto.setType(driverNotification.getType());

    driverNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(driverNotification.getSendDate()));

    return driverNotificationDto;
  }
}

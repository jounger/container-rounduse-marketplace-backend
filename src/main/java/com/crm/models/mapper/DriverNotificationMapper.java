package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.DriverNotification;
import com.crm.models.dto.DriverNotificationDto;
import com.crm.models.dto.ShippingInfoDto;

public class DriverNotificationMapper {

  public static DriverNotificationDto toDriverNotificationDto(DriverNotification driverNotification) {
    DriverNotificationDto driverNotificationDto = new DriverNotificationDto();

    driverNotificationDto.setId(driverNotification.getId());
    driverNotificationDto.setRecipient(driverNotification.getRecipient().getUsername());
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

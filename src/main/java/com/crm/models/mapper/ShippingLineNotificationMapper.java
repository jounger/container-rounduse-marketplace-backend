package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.ShippingLineNotification;
import com.crm.models.dto.CombinedDto;
import com.crm.models.dto.ShippingLineNotificationDto;

public class ShippingLineNotificationMapper {

  public static ShippingLineNotificationDto toShippingLineNotificationDto(
      ShippingLineNotification shippingLineNotification) {
    ShippingLineNotificationDto shippingLineNotificationDto = new ShippingLineNotificationDto();

    shippingLineNotificationDto.setId(shippingLineNotification.getId());
    shippingLineNotificationDto.setRecipient(shippingLineNotification.getRecipient().getUsername());
    shippingLineNotificationDto.setIsRead(shippingLineNotification.getIsRead());

    CombinedDto relatedResource = CombinedMapper.toCombinedDto(shippingLineNotification.getRelatedResource());
    shippingLineNotificationDto.setRelatedResource(relatedResource);

    shippingLineNotificationDto.setMessage(shippingLineNotification.getMessage());
    shippingLineNotificationDto.setAction(shippingLineNotification.getAction());
    shippingLineNotificationDto.setType(shippingLineNotification.getType());

    shippingLineNotificationDto.setSendDate(Tool.convertLocalDateTimeToString(shippingLineNotification.getSendDate()));

    return shippingLineNotificationDto;
  }
}

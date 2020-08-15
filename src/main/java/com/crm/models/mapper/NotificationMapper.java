package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Notification;
import com.crm.models.dto.NotificationDto;

public class NotificationMapper {
  public static NotificationDto toNotificationDto(Notification notification) {
    if (notification == null) {
      return null;
    }

    NotificationDto notificationDto = new NotificationDto();

    notificationDto.setId(notification.getId());
    notificationDto.setRecipient(UserMapper.toUserDto(notification.getRecipient()));
    notificationDto.setIsRead(notification.getIsRead());
    notificationDto.setIsHide(notification.getIsHide());
    notificationDto.setMessage(notification.getMessage());
    notificationDto.setTitle(notification.getTitle());
    notificationDto.setType(notification.getType());

    notificationDto.setSendDate(Tool.convertLocalDateTimeToString(notification.getSendDate()));

    return notificationDto;
  }
}

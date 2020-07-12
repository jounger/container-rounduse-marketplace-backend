package com.crm.models.mapper;

import com.crm.common.Tool;
import com.crm.models.Notification;
import com.crm.models.dto.NotificationDto;

public class NotificationMapper {
  public static NotificationDto toNotificationDto(Notification notification) {
    NotificationDto notificationDto = new NotificationDto();

    notificationDto.setId(notification.getId());
    notificationDto.setRecipient(notification.getRecipient().getUsername());
    notificationDto.setIsRead(notification.getIsRead());

    notificationDto.setMessage(notification.getMessage());
    notificationDto.setTitle(notification.getTitle());

    notificationDto.setSendDate(Tool.convertLocalDateTimeToString(notification.getSendDate()));

    return notificationDto;
  }
}

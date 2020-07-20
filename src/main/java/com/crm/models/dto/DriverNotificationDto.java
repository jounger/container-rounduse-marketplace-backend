package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverNotificationDto extends NotificationDto {

  private OutboundDto relatedResource;

  private String message;

  private String action;
}

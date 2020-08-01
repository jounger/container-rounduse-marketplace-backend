package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverNotificationDto extends NotificationDto {

  private ShippingInfoDto relatedResource;

  private String message;

  private String action;
}

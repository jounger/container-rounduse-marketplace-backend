package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingLineNotificationDto extends NotificationDto {

  private CombinedDto relatedResource;

  private String message;

  private String action;
}

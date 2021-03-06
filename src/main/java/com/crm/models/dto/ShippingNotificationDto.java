package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingNotificationDto extends NotificationDto {

  private ShippingInfoDto relatedResource;

  private String action;
}

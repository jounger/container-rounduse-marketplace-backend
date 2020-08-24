package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedNotificationDto extends NotificationDto {

  private CombinedDto relatedResource;

  private String action;
}

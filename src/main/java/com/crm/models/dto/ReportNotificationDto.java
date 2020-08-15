package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportNotificationDto extends NotificationDto {

  private ReportDto relatedResource;

  private String action;
}

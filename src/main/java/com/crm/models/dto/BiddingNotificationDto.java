package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingNotificationDto extends NotificationDto {

  private BiddingDocumentDto relatedResource;

  private String action;
}

package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
  
  private Long id;
  
  private String recipient;
  
  private Boolean isRead;

}

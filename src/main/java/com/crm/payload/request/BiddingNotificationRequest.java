package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingNotificationRequest {
  
  private Long id;
  
  @NotBlank
  private Long recipient;
  
  private Boolean isRead;
  
  @NotBlank
  private Long relatedResource;
  
  @NotBlank
  private String message;
  
  @NotBlank
  private String type;

}

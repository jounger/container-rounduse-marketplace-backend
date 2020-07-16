package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingLineNotificationRequest {

  private Long id;

  // recipient as User.username
  @NotNull
  private String recipient;

  private Boolean isRead;

  private String title;

  private Boolean isHide;

  @NotNull
  private Long relatedResource;

  @NotBlank
  private String message;

  @NotBlank
  private String type;
}

package com.crm.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class BiddingNotificationRequest {
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
  private String action;

  @NotBlank
  private String type;

}

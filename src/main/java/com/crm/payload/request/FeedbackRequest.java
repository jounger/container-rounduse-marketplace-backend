package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

  private Long id;

  private Long report;

  private String sender;

  private String message;

  private Integer satisfactionPoints;
}

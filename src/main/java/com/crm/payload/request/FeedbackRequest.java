package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FeedbackRequest {

  private Long id;

  private Long report;

  private String sender;

  private String recipient;

  private String message;

  private Integer satisfactionPoints;
}

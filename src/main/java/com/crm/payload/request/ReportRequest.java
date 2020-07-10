package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {

  private Long id;

  private String sender;

  private Long report;

  private String title;

  private String detail;

  private String status;
}

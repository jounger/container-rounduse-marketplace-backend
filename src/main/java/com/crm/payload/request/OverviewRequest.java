package com.crm.payload.request;

import lombok.Getter;
import lombok.ToString;
import lombok.Setter;

@Getter
@Setter
@ToString
public class OverviewRequest {

  private String status;

  private String startDate;

  private String endDate;
}

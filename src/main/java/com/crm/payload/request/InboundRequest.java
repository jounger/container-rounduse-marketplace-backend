package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InboundRequest extends SupplyRequest {

  private BillOfLadingRequest billOfLading;

  @NotBlank
  private String pickupTime;

  private String emptyTime;

//  @NotBlank
  private String returnStation;
}

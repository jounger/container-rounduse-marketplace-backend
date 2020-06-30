package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundDto extends SupplyDto {

  private String emptyTime;

  private String pickupTime;

  private String returnStation;

  private BillOfLadingDto billOfLading;
}

package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundDto {

  private Long id;

  // shippingLine.companyCode
  private String shippingLine;

  // containerType.name
  private String containerType;

  private String status;

  private String emptyTime;

  private String pickupTime;

  private BillOfLadingDto billOfLading;
}

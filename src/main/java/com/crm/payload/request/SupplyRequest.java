package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyRequest {

  private Long id;

  // shippingLine as ShippingLine.companyCode
  @NotBlank
  private String shippingLine;

  // containerType as ContainerType.name
  @NotBlank
  private String containerType;
}

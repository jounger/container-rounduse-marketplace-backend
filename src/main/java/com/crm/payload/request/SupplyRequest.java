package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyRequest {

  private Long id;

  @NotBlank
  private String shippingLine;

  @NotBlank
  private String containerType;
}

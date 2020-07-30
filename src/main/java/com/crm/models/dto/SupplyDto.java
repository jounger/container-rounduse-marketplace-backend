package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyDto {

  private Long id;

  private String code;

  private String shippingLine;

  private String containerType;

}

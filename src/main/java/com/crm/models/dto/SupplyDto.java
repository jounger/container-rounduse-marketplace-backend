package com.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyDto {
  
  private Long id;
  
  @JsonProperty("shipping_line")
  private String shippingLine;
  
  @JsonProperty("container_type")
  private String containerType;
  
  private String status;
}

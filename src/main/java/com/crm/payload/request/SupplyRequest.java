package com.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyRequest {
  
  private Long id;
  
  @JsonProperty("shipping_line_name")
  private String shippingLineName;
  
  @JsonProperty("container_type")
  private String containerType;
  
  private String status;
}

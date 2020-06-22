package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplyRequest {
  
  private Long id;
  
  private String shippingLine;
  
  private String containerType;
  
  private String status;
}

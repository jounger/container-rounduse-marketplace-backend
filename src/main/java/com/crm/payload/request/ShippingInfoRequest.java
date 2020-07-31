package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingInfoRequest {

  private Long id;
  
  private Long combined;
  
  private Long outbound;
  
  private Long container;
  
  private String status;
}

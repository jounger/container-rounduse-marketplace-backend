package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ShippingInfoRequest {

  private Long id;
  
  private Long contract;
  
  private Long outbound;
  
  private Long container;
  
  private String status;
}

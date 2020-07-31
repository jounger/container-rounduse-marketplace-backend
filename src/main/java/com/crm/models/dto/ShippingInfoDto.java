package com.crm.models.dto;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class ShippingInfoDto {
  
  private Long id;
  
  private String supplyCode;
  
  private String containerNumber;
  
  private String status;
}

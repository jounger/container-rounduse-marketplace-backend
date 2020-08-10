package com.crm.models.dto;

import lombok.Setter;

import lombok.Getter;

@Getter
@Setter
public class ShippingInfoDto {
  
  private Long id;
  
  private OutboundDto outbound;
  
  private ContainerDto container;
  
  private String status;
}

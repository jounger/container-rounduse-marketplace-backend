package com.crm.models.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutboundDto extends SupplyDto{
  
  private Long merchantId;
  
  private Set<String> categoryList;
  
  private String packingTime;
  
  private String packingStation;
  
  private String bookingNumber;
  
  private String laytime;
  
  private String cutOffTime;
  
  private float payload;
  
  private String unitOfMeasurement;
  
  private boolean fcl;
  
  private String portOfLoading;
  
}

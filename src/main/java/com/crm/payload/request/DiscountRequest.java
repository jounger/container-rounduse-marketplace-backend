package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountRequest {
  
  private Long id;
  
  private String code;
  
  private String detail;
  
  private String currency;
  
  private float percent;
  
  private float maximumDiscount;
  
  private String expiredDate;
}

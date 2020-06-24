package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountDto {
  
  private Long id;
  
  private String code;
  
  private String detail;
  
  private String currency;
  
  private float percent;
  
  private float maximumDiscount;
  
  private String expiredDate;
}

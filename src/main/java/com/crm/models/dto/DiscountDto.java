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
  
  private Double percent;
  
  private Double maximumDiscount;
  
  private String expiredDate;
}

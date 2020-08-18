package com.crm.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiscountRequest {
  
  private Long id;
  
  @NotBlank
  private String code;
  
  private String detail;
  
  private String currency;
  
  private Double percent;
  
  private Double maximumDiscount;
  
  private String expiredDate;
}

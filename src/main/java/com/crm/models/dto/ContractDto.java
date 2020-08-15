package com.crm.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {

  private Long Id;

  private Double price;

  private DiscountDto discountCode;

  private Double finesAgainstContractViolation;

  private Boolean required;

}

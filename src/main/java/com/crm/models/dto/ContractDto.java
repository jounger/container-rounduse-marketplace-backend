package com.crm.models.dto;

import java.util.ArrayList;
import java.util.List;

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

  private SupplierDto sender;

  private Double price;

  private DiscountDto discountCode;

  private Double finesAgainstContractViolation;

  private Boolean required;

  private String creationDate;

  private List<RatingDto> ratings = new ArrayList<>();

}

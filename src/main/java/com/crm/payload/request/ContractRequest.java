package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractRequest {

  private Long id;

  private Long combined;

  private Double price;

  private Double finesAgainstContractViolations;

  private String discountCode;

  private Boolean required;

  private List<Long> containers = new ArrayList<>();

}

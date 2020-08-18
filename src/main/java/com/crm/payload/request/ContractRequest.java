package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContractRequest {

  private Long id;

  private Long combined;

  private String supplier;

  private Double price;

  private Double finesAgainstContractViolations;

  private String discountCode;

  private Boolean required;

  private List<Long> containers = new ArrayList<>();

}

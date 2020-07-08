package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractRequest {
  
  private Long id;
  
  private Long combined;
  
  private Integer finesAgainstContractViolations;
  
  private Boolean required;

}

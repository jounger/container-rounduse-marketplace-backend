package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedDto {

  private Long id;
  
  private BidDto bid;
  
  private Boolean isCanceled;
  
  private ContractDto contract;
}

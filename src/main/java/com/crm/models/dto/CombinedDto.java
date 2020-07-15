package com.crm.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedDto {

  private Long id;
  
  private BidDto bid;
  
  private String status;
  
  private ContractDto contract;
}

package com.crm.models.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {
 
  //forwarder username
  private String bidder; 

  private List<ContainerDto> containers = new ArrayList<>();

  private Double bidPrice;

  private String bidDate;

  private String bidValidityPeriod;
  
  private String dateOfDecision;
  
  private String status;
}

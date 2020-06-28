package com.crm.models.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {
 
  //forwarder username
  private String bidder; 

  private List<ContainerDto> containers;

  private Double bidPrice;

  private String bidDate;

  private String bidValidityPeriod;
  
  private String status;
}

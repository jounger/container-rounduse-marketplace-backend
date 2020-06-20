package com.crm.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {
 
  private String bidder; 

  private ContainerDto container;

  @JsonProperty(value = "bid_price")
  private float bidPrice;
  
  @JsonProperty(value = "current_bid_price")
  private float currentBidPrice;

  @JsonProperty(value = "bid_date")
  private String bidDate;

  @JsonProperty(value = "bid_validity_period")
  private String bidValidityPeriod;
  
  @JsonProperty(value = "status")
  private String status;
}

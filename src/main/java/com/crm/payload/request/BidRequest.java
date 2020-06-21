package com.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {
  
  private Long id;
  
  @JsonProperty(value = "bidding_document_id")
  private Long biddingDocumentId;
  
  @JsonProperty(value = "fowarder_id")
  private Long fowarderId;
  
  @JsonProperty(value = "container_id")
  private Long containerId;
  
  @JsonProperty(value = "bid_price")
  private Float bidPrice;
  
  @JsonProperty(value = "current_bid_price")
  private Float currentBidPrice;
  
  @JsonProperty(value = "bid_date")
  private String bidDate;
  
  @JsonProperty(value = "bid_validity_period")
  private String bidValidityPeriod;
  
  @JsonProperty(value = "ebid_status_name")
  private Float eBidStatusName;
}

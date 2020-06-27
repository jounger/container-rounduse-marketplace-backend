package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {
  
  private Long id;
  
  private Long biddingDocumentId;
  
  private Long fowarderId;
  
  private Long containerId;
  
  private Double bidPrice;
  
  private Double currentBidPrice;
  
  private String bidDate;
  
  private String bidValidityPeriod;
  
  private String eBidStatusName;
}

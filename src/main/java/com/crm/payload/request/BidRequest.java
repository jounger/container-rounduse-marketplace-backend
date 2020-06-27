package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {
  
  private Long id;
  
  private Long biddingDocumentId;
  
  private Long fowarderId;
  
  private List<Long> containerId = new ArrayList<>();
  
  private Double bidPrice;
  
  private String bidDate;
  
  private String bidValidityPeriod;
  
  private String eBidStatusName;
}

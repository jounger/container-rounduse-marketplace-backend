package com.crm.payload.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentRequest {
  
  private Long id;
  
  private Long merchantId;
  
  private Long outBoundId;
  
  private Boolean isMultipleAward;
  
  private List<Long> bids;
  
  private String bidOpening;
  
  private String bidClosing;
  
  private String dateOfDecision;
  
  private String currencyOfPayment; 
  
  private Double bidPackagePrice;
  
  private Double bidFloorPrice;
  
  private String bidDiscountCode;
  
  private Double priceLeadership;
  
}

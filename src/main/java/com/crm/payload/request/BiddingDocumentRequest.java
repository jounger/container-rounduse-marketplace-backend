package com.crm.payload.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentRequest {
  
  private Long id;
  
  private Long merchantId;
  
  private Long consignmentId;
  
  private List<Long> bids;
  
  private String bidOpening;
  
  private String bidClosing;
  
  private String currencyOfPayment;
  
  private String bidPackagePrice;
  
  private String bidFloorPrice;
  
  private String bidStep;
  
  private String bidDiscountCode;
  
  private String priceLeaderShip;
  
}

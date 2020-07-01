package com.crm.payload.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentRequest {

  private Long id;

  //merchant as Merchant.username
  private String offeree;

  private Long outbound;

  private Boolean isMultipleAward;

  // List id of bid entities
  private List<Long> bids;

  private String bidOpening;

  private String bidClosing;

  private String currencyOfPayment;

  private Double bidPackagePrice;

  private Double bidFloorPrice;
  
  private String bidDiscountCode;

  private Double priceLeadership;

}

package com.crm.payload.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

  private Double priceLeadership;

  private String status;
}

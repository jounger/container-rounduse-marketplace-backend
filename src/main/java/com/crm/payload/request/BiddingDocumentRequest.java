package com.crm.payload.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentRequest {
  
  private Long id;
  
  @JsonProperty("merchant_id")
  private Long merchantId;
  
  @JsonProperty("consignment_id")
  private Long consignmentId;
  
  private List<Long> bids;
  
  @JsonProperty("bid_opening")
  private String bidOpening;
  
  @JsonProperty("bid_closing")
  private String bidClosing;
  
  @JsonProperty("currency_of_payment")
  private String currencyOfPayment;
  
  @JsonProperty("bid_package_price")
  private Float bidPackagePrice;
  
  @JsonProperty("bid_floor_price")
  private Float bidFloorPrice;
  
  @JsonProperty("bid_step")
  private Float bidStep;
  
  @JsonProperty("bid_discount_code")
  private String bidDiscountCode;
  
  @JsonProperty("price_leadership")
  private Float priceLeadership;
  
}

package com.crm.models.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentDto {
  
  //merchant username
  private String merchant;
  
  private OutboundDto consignment;
  
  private List<BidDto> bids;
  
  @JsonProperty(value = "currency_of_payment")
  private String currencyOfPayment;
  
  @JsonProperty(value = "bid_opening")
  private String bidOpening;
  
  @JsonProperty(value = "bid_closing")
  private String bidClosing;
  
  @JsonProperty(value = "bid_package_price")
  private float bidPackagePrice;
  
  @JsonProperty(value = "bid_floor_price")
  private float bidFloorPrice;
  
  @JsonProperty(value = "bid_step")
  private float bidStep;
  
  @JsonProperty(value = "bid_discount_code")
  private String bidDiscountCode;
  
  @JsonProperty(value = "price_leadership")
  private float priceLeadership;
}

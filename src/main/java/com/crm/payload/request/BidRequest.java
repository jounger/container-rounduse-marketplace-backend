package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {

  private Long id;

  //biddingDocument as BiddingDocument.id
  private Long biddingDocument;

  //forwarder as Forwarder.username
  private String bidder;

  //containers as List of Container.id
  private List<Long> containers = new ArrayList<>();

  private Double bidPrice;

  private String bidDate;

  private String bidValidityPeriod;

  private String dateOfDecision;

  // EnumBidStatus
  private String status;
}

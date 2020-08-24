package com.crm.payload.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BidRequest {

  private Long id;

  // forwarder as Forwarder.username
  private String bidder;

  // containers as List of Container.id
  private List<Long> containers = new ArrayList<>();

  private Double bidPrice;

  private String bidDate;

  private String freezeTime;

  private String validityPeriod;

  private String dateOfDecision;

  // EnumBidStatus
  private String status;
}

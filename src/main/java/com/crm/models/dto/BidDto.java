package com.crm.models.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidDto {

  private Long id;

  // forwarder username
  private ForwarderDto bidder;

  private List<ContainerDto> containers = new ArrayList<>();

  private Double bidPrice;

  private String bidDate;

  private String freezeTime;

  private String validityPeriod;

  private String dateOfDecision;

  private String status;
}

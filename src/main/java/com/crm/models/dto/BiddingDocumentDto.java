package com.crm.models.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BiddingDocumentDto {

  private Long id;

  // merchant username
  private String offeree;

  private OutboundDto outbound;

  private Boolean isMultipleAward;

  private List<BidDto> bids;

  private String currencyOfPayment;

  private String bidOpening;

  private String bidClosing;

  private Double bidPackagePrice;

  private Double bidFloorPrice;

  private Double priceLeadership;

  private String status;
}

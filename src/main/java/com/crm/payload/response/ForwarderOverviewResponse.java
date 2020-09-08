package com.crm.payload.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForwarderOverviewResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private int inboundQty;

  private int containerQty;

  private int biddingContainerQty;

  private int combinedContainerQty;

  private int deliveredContainerQty;

  private int bidQty;

  private int successfulBidQty;

  private int failBidQty;

  private int receivedContractQty;

  private int getPaidContractQty;

  private int unpaidContractQty;
}

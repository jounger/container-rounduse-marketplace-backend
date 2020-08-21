package com.crm.payload.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperatorOverviewResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private int outboundQty;

  private int inboundQty;

  private int containerQty;

  private int biddingDocumentQty;

  private int pendingBiddingDocumentQty;

  private int combinedBiddingDocumentQty;

  private int bidQty;

  private int pendingBidQty;

  private int combinedBidQty;

  private int containerBidQty;

  private int pendingContainerQty;

  private int combinedContainerQty;

  private int contractQty;

  private int paidContractQty;

  private int unpaidContractQty;

  private int reportQty;

  private int newMemberQty;
}

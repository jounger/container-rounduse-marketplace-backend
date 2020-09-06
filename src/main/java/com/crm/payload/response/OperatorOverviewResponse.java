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

  private int biddingOutboundQty;

  private int combinedOutboundQty;

  private int deliveredOutboundQty;

  private int inboundQty;

  private int biddingDocumentQty;

  private int biddingBiddingDocumentQty;

  private int combinedBiddingDocumentQty;

  private int bidQty;

  private int pendingBidQty;

  private int acceptedBidQty;

  private int containerQty;

  private int containerBidQty;

  private int biddingContainerQty;

  private int combinedContainerQty;

  private int deliveredContainerQty;

  private int contractQty;

  private int paidContractQty;

  private int unpaidContractQty;

  private int reportQty;

  private int resolvedReportQty;

  private int pendingReportQty;

  private int newMemberQty;

  private int unapprovedRegistration;
}

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
public class MerchantOverviewResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private int outboundQty;

  private int biddingOutboundQty;

  private int combinedOutboundQty;

  private int deliveredOutbountQty;

  private int contractQty;

  private int paidContractQty;

  private int unpaidContractQty;
}

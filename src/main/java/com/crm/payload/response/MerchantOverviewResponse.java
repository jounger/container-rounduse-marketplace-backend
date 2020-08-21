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

  private int biddedOutboundQty;

  private int pendingOutboundQty;

  private int combinedOutbountQty;

  private int contractQty;

  private int paidContractQty;

  private int unpaidContractQty;
}

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
public class ShippingLineOverviewResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer containerQty;

  private Integer biddingContainerQty;

  private Integer combinedContainerQty;

  private Integer deliveredContainerQty;
}

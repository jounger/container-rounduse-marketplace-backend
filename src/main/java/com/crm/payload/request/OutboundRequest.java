package com.crm.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutboundRequest extends SupplyRequest {

  private BookingRequest booking;

  private String goodsDescription;

  private String packingTime;

  private String packingStation;

  private Double payload;

  private String unitOfMeasurement;

  private String status;
}
